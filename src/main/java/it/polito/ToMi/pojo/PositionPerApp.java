package it.polito.ToMi.pojo;

import java.util.Date;

public class PositionPerApp {

  private String userId;
  private Date date;
  private double lat;
  private double lng;
  private int mode;
  
  public PositionPerApp(){
    
  }
  
  public PositionPerApp(DetectedPosition p) {
    this.userId = p.getUserId();
    this.date = p.getTimestamp();
    if(p.getPosition()!=null){
      this.lat = p.getPosition().getLat();
      this.lng = p.getPosition().getLng();
    }
    this.mode = p.getMode();
  }

  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }
  public Date getDate() {
    return date;
  }
  public void setDate(Date date) {
    this.date = date;
  }
  public double getLat() {
    return lat;
  }
  public void setLat(double lat) {
    this.lat = lat;
  }
  public double getLng() {
    return lng;
  }
  public void setLng(double lng) {
    this.lng = lng;
  }
  public int getMode() {
    return mode;
  }
  public void setMode(int mode) {
    this.mode = mode;
  }
  
  
}
