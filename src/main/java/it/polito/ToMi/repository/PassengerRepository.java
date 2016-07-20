package it.polito.ToMi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.Passenger;

public interface PassengerRepository extends MongoRepository<Passenger, String>, CustomPassengerRepository{

	public Passenger findByUserId(String userId);

}
