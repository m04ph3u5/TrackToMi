package it.polito.ToMi.pojo;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DetectedPosition {

  @Id
  private String id;
  private String deviceId;
  private Position position;
  private Date timestamp;
  private int mode;
  private String beaconId;
  @Indexed
  private String userId;
  private Integer userMode;
  private boolean userInteraction;

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getDeviceId() {
    return deviceId;
  }
  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }
  public Position getPosition() {
    return position;
  }
  public void setPosition(Position position) {
    this.position = position;
  }
  public Date getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }
  public int getMode() {
    return mode;
  }
  public void setMode(int mode) {
    this.mode = mode;
  }
  public String getBeaconId() {
    return beaconId;
  }
  public void setBeaconId(String beaconId) {
    this.beaconId = beaconId;
  }
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }
  public Integer getUserMode() {
    return userMode;
  }
  public void setUserMode(Integer userMode) {
    this.userMode = userMode;
  }
  public boolean isUserInteraction() {
    return userInteraction;
  }
  public void setUserInteraction(boolean userInteraction) {
    this.userInteraction = userInteraction;
  }

}
