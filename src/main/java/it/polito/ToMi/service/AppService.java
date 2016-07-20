package it.polito.ToMi.service;

import java.util.List;

import it.polito.ToMi.clustering.UserCluster;
import it.polito.ToMi.exception.BadRequestException;
import it.polito.ToMi.exception.NotFoundException;
import it.polito.ToMi.pojo.Bus;
import it.polito.ToMi.pojo.BusStop;
import it.polito.ToMi.pojo.Comment;
import it.polito.ToMi.pojo.DailyData;
import it.polito.ToMi.pojo.DetectedPosition;
import it.polito.ToMi.pojo.Passenger;
import it.polito.ToMi.pojo.RunDTO;
import it.polito.ToMi.pojo.RunDetail;
import it.polito.ToMi.pojo.TransportTime;

public interface AppService {

	public void saveDetectedPosition(List<DetectedPosition> position, Passenger passenger);

	public List<DetectedPosition> getMyPositions(String userId, long start, long end);

	public List<BusStop> getAllBusStop();

	public List<Bus> getAllBus();

	public void saveComments(List<Comment> comments, String userId);

	public List<Comment> getComments(String lastId);

	public DailyData getDailyData(String passengerId);
	
	public void saveAnswerToComments(String id, List<Comment> answers, String userId) throws BadRequestException ;

	public List<RunDTO> getRuns();

	public List<RunDetail> getRunDetails(long timestamp, String passengerId);

	public Passenger getPassenger(String userEmail) throws NotFoundException;

	/**
	 * @param id
	 * @return
	 * @throws NotFoundException 
	 */
	public List<TransportTime> getTransportTime(String id) throws NotFoundException;

	/**
	 * @param id
	 * @return
	 */
	public List<UserCluster> getUserCluster(String id);
}
