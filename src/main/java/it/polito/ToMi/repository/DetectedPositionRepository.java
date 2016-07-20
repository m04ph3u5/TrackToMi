package it.polito.ToMi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.DetectedPosition;

public interface DetectedPositionRepository extends MongoRepository<DetectedPosition, String>, CustomDetectedPositionRepository{



}
