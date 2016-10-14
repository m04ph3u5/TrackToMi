package it.polito.ToMi.pojo;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document
public class Passenger {

  @Id
  private String id;
  private String deviceId;

  @Indexed
  private String userId;
  private Date registrationDate;

  private PassengerProfile profile;


  private int runs, minutes, serviceTime;

  @JsonIgnore
  private Date lastTimeUpdate;
  @JsonIgnore
  private Date lastPosition;

  public Passenger(){
    registrationDate = new Date();
  }

  public Passenger(User u){
    this();
    this.setDeviceId(u.getDeviceId());
    this.setUserId(u.getId());
  }

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
  public Date getRegistrationDate() {
    return registrationDate;
  }
  public void setRegistrationDate(Date registrationDate) {
    this.registrationDate = registrationDate;
  }
  public int getRuns() {
    return runs;
  }
  public void setRuns(int runs) {
    this.runs = runs;
  }
  public int getMinutes() {
    return minutes;
  }
  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }
  public int getServiceTime() {
    return serviceTime;
  }
  public void setServiceTime(int serviceTime) {
    this.serviceTime = serviceTime;
  }
  public Date getLastTimeUpdate() {
    return lastTimeUpdate;
  }
  public void setLastTimeUpdate(Date lastTimeUpdate) {
    this.lastTimeUpdate = lastTimeUpdate;
  }
  public Date getLastPosition() {
    return lastPosition;
  }
  public void setLastPosition(Date lastPosition) {
    this.lastPosition = lastPosition;
  }
  public PassengerProfile getProfile() {
    return profile;
  }
  public void setProfile(PassengerProfile profile) {
    this.profile = profile;
  }

}
