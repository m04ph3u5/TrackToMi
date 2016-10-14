package it.polito.ToMi.pojo.movement;

import javax.validation.constraints.Size;

public class PublicTransport {
  
  @Size(max=30)
  private String ticketType;

  public String getTicketType() {
    return ticketType;
  }

  public void setTicketType(String ticketType) {
    this.ticketType = ticketType;
  }
  
  
}
