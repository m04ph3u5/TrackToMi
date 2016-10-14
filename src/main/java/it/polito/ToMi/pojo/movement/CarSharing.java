package it.polito.ToMi.pojo.movement;

import javax.validation.constraints.Size;

public class CarSharing {

  @Size(max=30)
  private String company;

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }
  
  
}
