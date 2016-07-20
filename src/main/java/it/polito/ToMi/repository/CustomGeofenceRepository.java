package it.polito.ToMi.repository;

import java.util.List;

import it.polito.ToMi.pojo.Geofence;

public interface CustomGeofenceRepository {
	
	public Geofence lastGeofenceOfUser(String userId);
	public List<Geofence> findByUserIdSortedByEnterTimestamp(String userId);


}
