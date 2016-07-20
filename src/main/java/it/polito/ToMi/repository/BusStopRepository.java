package it.polito.ToMi.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.BusStop;

public interface BusStopRepository extends MongoRepository<BusStop, String>, CustomBusStopRepository{

	public List<BusStop> findByIdRun(String idRun, Sort sort);

}


