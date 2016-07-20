/**
 * 
 */
package it.polito.ToMi.pojo;

/**
 * @author m04ph3u5
 *
 */
public class TransportTime {
	
	long onVehicle, onBicycle, onFoot;
	long onVehicleEffective, onBicycleEffective, onFootEffective;
	
	public long getOnVehicle() {
		return onVehicle;
	}

	public void setOnVehicle(long onVehicle) {
		this.onVehicle = onVehicle;
	}

	public long getOnBicycle() {
		return onBicycle;
	}

	public void setOnBicycle(long onBicycle) {
		this.onBicycle = onBicycle;
	}

	public long getOnFoot() {
		return onFoot;
	}

	public void setOnFoot(long onFoot) {
		this.onFoot = onFoot;
	}
	
	public void addMinuteToVehicle(long minute){
		onVehicle+=minute;
	}

	public void addMinuteToBicycle(long minute){
		onBicycle+=minute;
	}
	
	public void addMinuteToFoot(long minute){
		onFoot+=minute;
	}

	public long getOnVehicleEffective() {
		return onVehicleEffective;
	}

	public void setOnVehicleEffective(long onVehicleEffective) {
		this.onVehicleEffective = onVehicleEffective;
	}

	public long getOnBicycleEffective() {
		return onBicycleEffective;
	}

	public void setOnBicycleEffective(long onBicycleEffective) {
		this.onBicycleEffective = onBicycleEffective;
	}

	public long getOnFootEffective() {
		return onFootEffective;
	}

	public void setOnFootEffective(long onFootEffective) {
		this.onFootEffective = onFootEffective;
	}
	
	public void addMinuteToEffectiveVehicle(long minute){
		onVehicleEffective+=minute;
	}

	public void addMinuteToEffectiveBicycle(long minute){
		onBicycleEffective+=minute;
	}
	
	public void addMinuteToEffectiveFoot(long minute){
		onFootEffective+=minute;
	}
}
