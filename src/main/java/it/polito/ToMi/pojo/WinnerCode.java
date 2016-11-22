package it.polito.ToMi.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document
public class WinnerCode {

  @Id
  @JsonIgnore
  private String id;
  @Indexed(unique=true)
  private String code;
  private boolean used;
  
  public WinnerCode(){
    
  }
  
  public WinnerCode(String code){
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public boolean isUsed() {
    return used;
  }

  public void setUsed(boolean used) {
    this.used = used;
  }

}
