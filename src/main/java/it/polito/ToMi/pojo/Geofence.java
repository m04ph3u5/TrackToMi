package it.polito.ToMi.pojo;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Geofence implements Clusterable, Comparable<Geofence>{

	private String id;
	@Indexed
	private String userId;
	private double[] point;
	@Indexed
	private long enterTimestamp;
	private long exitTimestamp;
	
	public Geofence(){
		point = new double[2];
	}
	
	@Override
	public double[] getPoint() {
		return point;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getEnterTimestamp() {
		return enterTimestamp;
	}

	public void setEnterTimestamp(long enterTimestamp) {
		this.enterTimestamp = enterTimestamp;
	}

	public long getExitTimestamp() {
		return exitTimestamp;
	}

	public void setExitTimestamp(long exitTimestamp) {
		this.exitTimestamp = exitTimestamp;
	}
	
	public long getDurationOfStay(){
		return exitTimestamp-enterTimestamp;
	}

	public void setPoint(double[] point) {
		this.point = point;
	}
	
	public void setPoint(Position p) {
		point[0] = p.getLat();
		point[1] = p.getLng();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Geofence o) {
		if(this.enterTimestamp<o.enterTimestamp)
			return -1;
		else if(this.enterTimestamp>o.enterTimestamp)
			return 1;
		else
			return 0;
	}
	
	
}
