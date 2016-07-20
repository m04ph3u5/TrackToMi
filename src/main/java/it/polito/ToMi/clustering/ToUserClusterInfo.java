/**
 * 
 */
package it.polito.ToMi.clustering;

/**
 * @author m04ph3u5
 *
 */
public class ToUserClusterInfo {
	
	private Integer idNum;
	private long startTime;
	private long endTime;
	
	public ToUserClusterInfo(){}
	
	public ToUserClusterInfo(Integer idNum, long startTime, long endTime){
		this();
		this.idNum = idNum;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public Integer getIdNum() {
		return idNum;
	}
	public void setIdNum(Integer idNum) {
		this.idNum = idNum;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public long getTravelTime(){
		return endTime-startTime;
	}
	

}
