package it.polito.ToMi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.TemporaryTravel;

public interface TemporaryTravelRepository extends MongoRepository<TemporaryTravel, String>{

	public TemporaryTravel findByPassengerIdAndDeviceId(String passengerId, String deviceId);
}
