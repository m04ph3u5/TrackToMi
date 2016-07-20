package it.polito.ToMi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.BusTravel;

public interface BusTravelRepository extends MongoRepository<BusTravel, String>{

}
