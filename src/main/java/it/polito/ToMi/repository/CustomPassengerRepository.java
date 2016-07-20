package it.polito.ToMi.repository;

import java.util.Date;
import java.util.List;

import it.polito.ToMi.pojo.Passenger;

public interface CustomPassengerRepository {
	/**
	 * @param time
	 * @return
	 */
	public List<Passenger> findUpdatable(Date time);
}
