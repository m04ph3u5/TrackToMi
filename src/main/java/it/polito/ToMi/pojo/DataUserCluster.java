package it.polito.ToMi.pojo;

import java.util.List;

import org.apache.commons.math3.ml.clustering.Cluster;

import it.polito.ToMi.clustering.ToUserClusterInfo;
import it.polito.ToMi.clustering.UserCluster;

public class DataUserCluster {
  
  private Cluster<Geofence> cluster;
  private double[] centroid;
  private String userId;
  private int idNum;
  private long duration;
  private List<ToUserClusterInfo> toCluster;
  
  public DataUserCluster(UserCluster uc){
    this.cluster = uc.getCluster();
    this.centroid = uc.getCentroid();
    this.userId = uc.getUserId();
    this.idNum = uc.getIdNum();
    this.duration = uc.getAverageDurationOfStay();
    this.toCluster = uc.getToCluster();
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

  public int getIdNum() {
    return idNum;
  }

  public void setIdNum(int idNum) {
    this.idNum = idNum;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public List<ToUserClusterInfo> getToCluster() {
    return toCluster;
  }

  public void setToCluster(List<ToUserClusterInfo> toCluster) {
    this.toCluster = toCluster;
  }
  
  
}
