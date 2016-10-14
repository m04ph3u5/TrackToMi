package it.polito.ToMi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.PassengerProfile;

public interface PassengerProfileRepository extends MongoRepository<PassengerProfile, String>{

}
