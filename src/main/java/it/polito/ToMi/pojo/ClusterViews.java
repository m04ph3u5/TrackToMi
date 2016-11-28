package it.polito.ToMi.pojo;

import java.util.List;

public class ClusterViews {

  private String userId;
  private List<DataUserCluster> userClusters;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public List<DataUserCluster> getUserClusters() {
    return userClusters;
  }

  public void setUserClusters(List<DataUserCluster> userClusters) {
    this.userClusters = userClusters;
  }

}
