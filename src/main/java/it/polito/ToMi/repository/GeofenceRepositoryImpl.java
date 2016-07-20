package it.polito.ToMi.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import it.polito.ToMi.pojo.Geofence;

public class GeofenceRepositoryImpl implements CustomGeofenceRepository{
	
	@Autowired
	private MongoOperations mongoOp;

	@Override
	public Geofence lastGeofenceOfUser(String userId) {
		Query q = new Query();
		q.addCriteria(Criteria.where("userId").is(userId));
		q.with(new Sort(Sort.Direction.DESC, "enterTimestamp"));
		q.limit(1);
		List<Geofence> geofences = mongoOp.find(q, Geofence.class);
		if(geofences!=null && !geofences.isEmpty())
			return geofences.get(0);
		else 
			return null;
	}

	@Override
	public List<Geofence> findByUserIdSortedByEnterTimestamp(String userId) {
		Query q = new Query();
		q.addCriteria(Criteria.where("userId").is(userId));
		q.with(new Sort(Sort.Direction.ASC, "enterTimestamp"));
		return mongoOp.find(q, Geofence.class);
	}

}
