package it.polito.ToMi.pojo;

import java.util.Calendar;
import java.util.Date;

import org.springframework.data.annotation.Transient;

public class InfoPosition {

	private Date timestamp;
	private Position position;
	@Transient
	private int mode;
	
	public InfoPosition(){
		super();
	}
	
	public InfoPosition(DetectedPosition p){
		this();
		this.timestamp = p.getTimestamp();
		this.position = p.getPosition();
		this.mode = p.getMode();
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	
	public long getHour(){
		Calendar c = Calendar.getInstance();
		c.setTime(timestamp);
		int h = c.get(Calendar.HOUR_OF_DAY);
		int m = c.get(Calendar.MINUTE);
		return h*60*60*1000 + m*60*1000;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
}
