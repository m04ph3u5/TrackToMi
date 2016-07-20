/**
 * 
 */
package it.polito.ToMi.clustering;

import java.util.List;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author m04ph3u5
 *
 */
@Document
public class GlobalCluster {

	@Id
	private String id;
	private Cluster<UserCluster> cluster;
	private double[] centroid;
	private int idNum;
	private long averageDurationOfStay;
	private List<ToGlobalClusterInfo> toCluster; 
	private int numUsers;
	
	public GlobalCluster(){}
	
	public GlobalCluster(Cluster<UserCluster> cluster, int idNum){
		this();
		this.cluster = cluster;
		this.idNum = idNum;
		
		elaborateCentroid();
		elaborateAverageDurationOfStay();
	}

	public Cluster<UserCluster> getCluster() {
		return cluster;
	}

	public void setCluster(Cluster<UserCluster> cluster) {
		this.cluster = cluster;
	}

	public double[] getCentroid() {
		return centroid;
	}

	public void setCentroid(double[] centroid) {
		this.centroid = centroid;
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

	public void setAverageDurationOfStay(long averageDurationOfStay) {
		this.averageDurationOfStay = averageDurationOfStay;
	}

	public List<ToGlobalClusterInfo> getToCluster() {
		return toCluster;
	}

	public void setToCluster(List<ToGlobalClusterInfo> toCluster) {
		this.toCluster = toCluster;
	}

	public String getId() {
		return id;
	}
	
	public int getNumUsers() {
		return numUsers;
	}

	public void setNumUsers(int numUsers) {
		this.numUsers = numUsers;
	}

	private void elaborateCentroid() {
		if(cluster==null)
			return;
		
		List<UserCluster> points = cluster.getPoints();
		if(points==null || points.size()==0)
			return;
		
		double lat=0, lng=0;
		for(UserCluster p : points){
			lat+=p.getCentroid()[0];
			lng+=p.getCentroid()[1];
		}
		lat/=points.size();
		lng/=points.size();
		centroid = new double[2];
		centroid[0] = lat;
		centroid[1] = lng;
	}

	private void elaborateAverageDurationOfStay() {
		if(cluster==null)
			return;
		List<UserCluster> points = cluster.getPoints();
		if(points==null)
			return;
		
		long duration=0;
		int weight=0;
		
		for(UserCluster p : points){
			duration+=(p.getAverageDurationOfStay()*p.getCluster().getPoints().size());
			weight+=p.getCluster().getPoints().size();
		}
		averageDurationOfStay = duration/=weight;
	}
	

}
