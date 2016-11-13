package it.polito.ToMi.clustering;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

import javax.annotation.PostConstruct;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tc.async.api.PostInit;

import it.polito.ToMi.pojo.DetectedPosition;
import it.polito.ToMi.pojo.Geofence;
import it.polito.ToMi.pojo.Passenger;
import it.polito.ToMi.repository.DetectedPositionRepository;
import it.polito.ToMi.repository.GeofenceRepository;
import it.polito.ToMi.repository.GlobalClusterRepository;
import it.polito.ToMi.repository.PassengerRepository;
import it.polito.ToMi.repository.UserClusterRepository;

@Component
public class ScheduledTaskImpl {

	@Autowired
	private PassengerRepository passRepo;

	@Autowired
	private GeofenceRepository geofenceRepo;

	@Autowired
	private UserClusterRepository userClusterRepo;

	@Autowired
	private GlobalClusterRepository globalClusterRepo;

	@Autowired
	private DetectedPositionRepository detectedPosRepo;

	@Autowired
	private BlockingExecutor executor;

	@Value("${cluster.user.radius}")
	private double userRadius;

	@Value("${cluster.user.minPoints}")
	private int userMinPoints;

	@Value("${cluster.global.radius}")
	private double globalRadius;

	@Value("${cluster.global.minPoints}")
	private int globalMinPoints;

	@Value("${app.checkTime}")
	private int checkTime;

	private static final Logger logger = LogManager.getRootLogger();
	
	@Scheduled(cron = "${scheduledTask.cluster.cron}")
	public void usersClustering() {
		try {
			List<Passenger> passengers = passRepo.findAll();
			for(Passenger p : passengers)
				if(p.getUserId()!=null)
					executor.submit(new UserClusteringTask(p));

			executor.finalSubmit(new GlobalClusteringTask());
		} catch (RejectedExecutionException | InterruptedException e) {
			logger.error(e.getMessage());
		} finally {
		}
	}

	@Scheduled(cron= "${scheduledTask.updateTime.cron}")
	public void updateUtilizationTime(){
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.HOUR_OF_DAY, -1);
			cal.add(Calendar.MINUTE, -5);
			List<Passenger> toUpdate = passRepo.findUpdatable(cal.getTime());
			if(toUpdate==null || toUpdate.size()==0)
				return;
	
			for(Passenger p : toUpdate){
				if(p.getUserId()!=null)
					executor.submit(new UpdateTimeTask(p));
			}
		} catch (RejectedExecutionException | InterruptedException e) {
			logger.error(e.getMessage());
		}
	}

	private class UpdateTimeTask implements Runnable{

		private String userId;
		private Passenger passenger;

		public UpdateTimeTask(Passenger p){
			this.userId = p.getId();
			this.passenger = p;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			String threadName = Thread.currentThread().getName(); 
			logger.info(threadName+ " start update times for user "+userId);
			List<DetectedPosition> positions = null;
			Date lastUpdate = passenger.getLastTimeUpdate();	
			/*
			 * Sugli utenti già presenti nel sistema questa data sarà null, quindi la prima volta vado a fare una ricerca 
			 * su tutte le posizioni salvate per quell'utente, andando poi ad aggiornare questo campo.
			 * 
			 * */
			if(lastUpdate==null){ 
				positions = detectedPosRepo.findByUserId(userId, new Sort(Sort.Direction.ASC,"timestamp"));
			}else{
				positions = detectedPosRepo.findByUserIdAfterDate(userId, lastUpdate, new Sort(Sort.Direction.ASC,"timestamp"));
			}
			if(positions==null || positions.size()<2){
				logger.warn(threadName + " update user time failed for " + userId);		
				return;
			}

			long minutes = positions.get(positions.size()-1).getTimestamp().getTime() - positions.get(0).getTimestamp().getTime();
			minutes/=60000l;
			DetectedPosition before = positions.get(0);
			for(int i=1; i<positions.size(); i++){
				long interval = positions.get(i).getTimestamp().getTime()-before.getTimestamp().getTime();
				interval/=60000l;
				if(interval>checkTime)
					minutes-=interval;
				before = positions.get(i);
			}
			if(lastUpdate==null){
				if(minutes>0)
					passenger.setServiceTime((int)minutes);
			}else{
				if(minutes>0)
					passenger.setServiceTime(passenger.getServiceTime()+(int)(minutes));
			}
			//TODO represents service time in minute NOT in hours!!!
			passenger.setLastTimeUpdate(new Date());
			passRepo.save(passenger);
			logger.info(threadName+ " update times complete for user "+userId);
		}
	}


	private class UserClusteringTask implements Runnable{

		private String userId;

		public UserClusteringTask(Passenger passenger) {
			this.userId = passenger.getId();
		}

		@Override
		public void run() {
			String threadName = Thread.currentThread().getName(); 
			logger.info(threadName + " beginning work on " + userId);

			List<Geofence> geofences = geofenceRepo.findByUserIdSortedByEnterTimestamp(userId);
			if(geofences==null || geofences.size()==0){
				logger.warn(threadName+" interrputed. No geofences available for user "+userId);
				return;
			}

			DBSCANClusterer<Geofence> dbscan=new DBSCANClusterer<>(userRadius,userMinPoints);
			List<Cluster<Geofence>> clusters= dbscan.cluster(geofences);
			if (clusters==null || clusters.size()<2){
				logger.warn(threadName+" interrputed. No cluster (<2) available for user "+userId);
				return;
			}

			
			List<UserCluster> userClusters = new ArrayList<UserCluster>();
			Map<Geofence,Integer> map = new HashMap<Geofence, Integer>();
			for(int i=0; i<clusters.size(); i++){
				Cluster<Geofence> c = clusters.get(i);
				for(Geofence g : c.getPoints())
					map.put(g, i);
				userClusters.add(new UserCluster(c, userId, i));
			}

			for(UserCluster uc : userClusters){
				List<Geofence> points = uc.getCluster().getPoints();
				for(Geofence g : points){
					int i = Collections.binarySearch(geofences, g);
					if(i>=0 && i<geofences.size()-1){
						Geofence next = geofences.get(i+1);
						Integer idCluster = map.get(next);
						if(idCluster!=null && idCluster!=uc.getIdNum()){
							ToUserClusterInfo tcf = new ToUserClusterInfo(idCluster, g.getExitTimestamp(), next.getEnterTimestamp());
							uc.addIdToCluster(tcf);
						}
					}
				}
			}

			userClusterRepo.deleteByUserId(userId);
			userClusterRepo.save(userClusters);
			logger.info(threadName + " clustering complete for user " + userId);
		}

		/**
		 * @param userId
		 */
	}

	private class GlobalClusteringTask implements Runnable{


		@Override
		public void run() {
			String threadName = Thread.currentThread().getName(); 
			logger.info("  " + threadName + " beginning final work");
			List<UserCluster> points = userClusterRepo.findAll();
			if(points==null){
				logger.warn("Global cluster interrupted: no points availeble");
				return;

			}

			DBSCANClusterer<UserCluster> dbScan = new DBSCANClusterer<>(globalRadius, globalMinPoints);
			List<Cluster<UserCluster>> clusters = dbScan.cluster(points);
			if (clusters==null || clusters.size()<2){
				logger.warn("Global cluster interrupted: no one cluster resulting");
				return;
			}

			List<GlobalCluster> globalClusters = new ArrayList<GlobalCluster>();
			for(int i=0; i<clusters.size(); i++){
				Cluster<UserCluster> c = clusters.get(i);
				GlobalCluster gc = new GlobalCluster(c,i);
				Set<String> userIds = new HashSet<String>();
				for(UserCluster uc : c.getPoints())
					userIds.add(uc.getUserId());
				gc.setNumUsers(userIds.size());
				globalClusters.add(gc);
			}

			globalClusterRepo.deleteAll();
			globalClusterRepo.save(globalClusters);
			logger.info(threadName + " final work completed");
		}
	}


	/*CODE TO GENERATE GEOFENCE FROM DETECTED POSITION
	 * 	try{
			int created=0;
			System.out.println("STARTING CREATING TEST GEOFENCE at "+new Date());
			List<DetectedPosition> positions = posRepo.findAll();
			System.out.println(positions.size());
			for(int i=0; i<positions.size(); i++){
				System.out.println(i);
				DetectedPosition p = positions.get(i);
				if(p.getMode()==9 && !p.getUserEmail().isEmpty()){
					Geofence g = new Geofence();
					g.setPoint(p.getPosition());
					Passenger passenger = passRepo.findByEmail(p.getUserEmail());
					if(passenger==null){
						passenger = new Passenger();
						passenger.setEmail(p.getUserEmail());
						passenger.setDeviceId(p.getDeviceId());
						passRepo.save(passenger);
					}
					g.setUserId(passenger.getId());
					g.setEnterTimestamp(p.getTimestamp().getTime());
					if(i<positions.size()-1)
						g.setExitTimestamp(positions.get(i+1).getTimestamp().getTime());
					else
						g.setExitTimestamp(g.getEnterTimestamp());
					geofenceRepo.save(g);
					created++;
				}
			}
			System.out.println("CREATED "+created+" NEW GEOFENCES\nTask finished at "+new Date());
		}catch(Exception e){
			System.out.println("Exception: "+e.getMessage());
		}
	 * */
}
