package it.polito.ToMi.repository;

import java.util.Date;
import java.util.List;

import it.polito.ToMi.pojo.DayPassengerBusRun;
import it.polito.ToMi.pojo.Travel;

public interface CustomTravelRepository {
	
	List<DayPassengerBusRun> countToMiDailyBusTravel(String passengerId);
	List<DayPassengerBusRun> countMiToDailyBusTravel(String passengerId);
	List<Travel> findMyBusTravelInDay(Date start, Date end, String passengerId);
	List<Travel> findMyBusTravel(String id);
	/**
	 * @param id
	 * @return
	 */
	List<Travel> findMyTravel(String id);
	List<Travel> findMyTravelAfterDate(String passengerId, Date d);


}
