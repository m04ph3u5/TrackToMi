package it.polito.ToMi.pojo.movement;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class PrivateTransport {

  @Min(0)
  private int year;
  @Size(max=15)
  private String fuel;
  @Size(max=30)
  private String model;
  
  public int getYear() {
    return year;
  }
  public void setYear(int year) {
    this.year = year;
  }
  public String getFuel() {
    return fuel;
  }
  public void setFuel(String fuel) {
    this.fuel = fuel;
  }
  public String getModel() {
    return model;
  }
  public void setModel(String model) {
    this.model = model;
  }
  
  
}
