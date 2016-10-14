package it.polito.ToMi.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Bus {

  @Id
  private String id;
  private String idBus;
  private String idLine;
  private String beaconId;
  private String licensePlate;

  public String getIdBus() {
    return idBus;
  }
  public void setIdBus(String idBus) {
    this.idBus = idBus;
  }
  public String getIdLinea() {
    return idLine;
  }
  public void setIdLinea(String idLine) {
    this.idLine = idLine;
  }
  public String getId() {
    return id;
  }
  public String getIdLine() {
    return idLine;
  }
  public void setIdLine(String idLine) {
    this.idLine = idLine;
  }
  public String getBeaconId() {
    return beaconId;
  }
  public void setBeaconId(String beaconId) {
    this.beaconId = beaconId;
  }
  public String getLicensePlate() {
    return licensePlate;
  }
  public void setLicensePlate(String licensePlate) {
    this.licensePlate = licensePlate;
  }

}
