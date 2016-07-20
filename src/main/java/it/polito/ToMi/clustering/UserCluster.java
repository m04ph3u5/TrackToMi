package it.polito.ToMi.clustering;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.polito.ToMi.pojo.Geofence;

@Document
public class UserCluster implements Clusterable{
	
	private final static double LAT_SCALE = 110574d;
	private final static double LNG_SCALE = 111320d;
	private final static int NOISE_RADIUS = 100;
	

	@Id
	private String id;
	@JsonIgnore
	private Cluster<Geofence> cluster;
	private double[] centroid;
	@Indexed
	private String userId;
	private int idNum;
	private long averageDurationOfStay;
	private List<ToUserClusterInfo> toCluster;
	
	public UserCluster(){
		toCluster = new ArrayList<ToUserClusterInfo>();
	}
	
	public UserCluster(Cluster<Geofence> cluster, String userId, int idNum){
		this();
		this.cluster = cluster;
		this.userId = userId;
		this.idNum = idNum;
		elaborateCentroid();
		elaborateAverageDurationOfStay();
	}
	
	@Override
	public double[] getPoint() {
		return centroid;
	}

	public Cluster<Geofence> getCluster() {
		return cluster;
	}

	public void setCluster(Cluster<Geofence> cluster) {
		this.cluster = cluster;
	}

	public double[] getCentroid() {
		return centroid;
	}

	public void setCentroid(double[] centroid) {
		this.centroid = centroid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public int getIdNum() {
		return idNum;
	}

	public void setIdNum(int idNum) {
		this.idNum = idNum;
	}

	public long getAverageDurationOfStay() {
		return averageDurationOfStay;
	}
	
	public List<ToUserClusterInfo> getToCluster() {
		return toCluster;
	}

	public void setToCluster(List<ToUserClusterInfo> toCluster) {
		this.toCluster = toCluster;
	}

	public void addIdToCluster(ToUserClusterInfo tcf){
		this.toCluster.add(tcf);
	}

	/**
	 * @return
	 */
	private void elaborateAverageDurationOfStay() {
		if(cluster==null)
			return;
		List<Geofence> points = cluster.getPoints();
		if(points==null)
			return;
		
		long duration=0;
		
		for(Geofence g : points)
			duration+=g.getDurationOfStay();
		
		averageDurationOfStay = duration/=points.size();
	}
	
	private void elaborateCentroid() {
		if(cluster==null)
			return;
		
		double lat=0, lng=0;
		List<Geofence> points = cluster.getPoints();
		if(points.size()==0)
			return;
		else{
			for(Geofence g : points){
				lat+=g.getPoint()[0];
				lng+=g.getPoint()[1];
			}
			lat/=points.size();
			lng/=points.size();
			centroid = addNoise(lat,lng);
		}
	}

	/**
	 * @param lat
	 * @param lng
	 * @return
	 */
	private double[] addNoise(double lat, double lng) {
		
		double latMeter = lat*LAT_SCALE;
		double lngMeter = lng*LNG_SCALE * Math.cos((lat*Math.PI)/180);
		
		double noise = SimplexNoise.noise(latMeter, lngMeter);
		
		//radius approximation in degree
		double radiusDegree=NOISE_RADIUS/LNG_SCALE;
		double w = radiusDegree * Math.sqrt(Math.abs(noise));
		double t = 2*Math.PI*noise;
		
		double x = w * Math.cos(t);
		double y = w * Math.sin(t);
		
		double[] pos = new double[2];
		pos[0]=x+lat;
		pos[1]=y+lng;
		return pos;
	}

}
