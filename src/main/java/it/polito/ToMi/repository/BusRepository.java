package it.polito.ToMi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.Bus;

public interface BusRepository extends MongoRepository<Bus, String> {
	
	public Bus findByBeaconId(String beaconId);

}
