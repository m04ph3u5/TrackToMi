package it.polito.ToMi.pojo;

public class InfoTravel {

  private int minutes;
  private int runs;
  
  public InfoTravel(){}
  
  public InfoTravel(int runs, int minutes){
    this.runs = runs;
    this.minutes = minutes;
  }
  
  public int getMinutes() {
    return minutes;
  }
  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }
  public int getRuns() {
    return runs;
  }
  public void setRuns(int runs) {
    this.runs = runs;
  }
  
  
}
