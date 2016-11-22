package it.polito.ToMi.pojo;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class UsageRank {

  @Id
  private String id;
  private String passengerId;
  private Date timestamp;
  private boolean refused;
  private boolean accepted;
  private String code;

  public String getId() {
    return id;
  }

  public String getPassengerId() {
    return passengerId;
  }

  public void setPassengerId(String passengerId) {
    this.passengerId = passengerId;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public boolean isRefused() {
    return refused;
  }

  public void setRefused(boolean refused) {
    this.refused = refused;
  }

  public boolean isAccepted() {
    return accepted;
  }

  public void setAccepted(boolean accepted) {
    this.accepted = accepted;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }


}
