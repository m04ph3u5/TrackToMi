package it.polito.ToMi.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.geo.GeoResults;

import it.polito.ToMi.pojo.BusStop;
import it.polito.ToMi.pojo.InfoPosition;

public interface CustomBusStopRepository {
	GeoResults<BusStop> findNear(InfoPosition infoPosition, String idLine);
	Collection<? extends BusStop> findBetweenFirstAndLast(BusStop busStop, BusStop busStop2);
	public List<BusStop> findAllSortByIdRunAndIdLineAndIdProg();

}
