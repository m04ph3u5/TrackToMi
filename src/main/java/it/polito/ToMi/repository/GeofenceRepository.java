package it.polito.ToMi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.Geofence;

public interface GeofenceRepository extends MongoRepository<Geofence, String>, CustomGeofenceRepository{

}
