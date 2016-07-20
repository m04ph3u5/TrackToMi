package it.polito.ToMi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.Travel;

public interface TravelRepository extends MongoRepository<Travel, String>, CustomTravelRepository{

}
