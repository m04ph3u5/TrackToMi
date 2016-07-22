package it.polito.ToMi.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Stop {

	@Id
	private String id;
	private int passengers;
	private boolean myRoute;
	private String name;
	
	public Stop(){
		
	}
	
	public Stop(String id, int passengers, boolean myRoute, String name) {
		super();
		this.id = id;
		this.passengers = passengers;
		this.myRoute = myRoute;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getPassengers() {
		return passengers;
	}
	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}
	public boolean isMyRoute() {
		return myRoute;
	}
	public void setMyRoute(boolean myRoute) {
		this.myRoute = myRoute;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
