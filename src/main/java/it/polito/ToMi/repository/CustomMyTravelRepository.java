package it.polito.ToMi.repository;

import it.polito.ToMi.pojo.MyTravel;

import java.util.Date;
import java.util.List;

public interface CustomMyTravelRepository {

    MyTravel findLastByPassenger(String passengerId);

    List<MyTravel> findMyTravelAfterDate(String id, Date time);
}
