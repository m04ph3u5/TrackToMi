package it.polito.ToMi.repository;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import it.polito.ToMi.pojo.Passenger;

public class PassengerRepositoryImpl implements CustomPassengerRepository{
	
	@Autowired
	private MongoOperations mongoOp;

	/* (non-Javadoc)
	 * @see it.polito.ToMi.repository.CustomPassengerRepository#findUpdatable(java.util.Date)
	 */
	@Override
	public List<Passenger> findUpdatable(Date time) {
		Query q = new Query();
		q.addCriteria(Criteria.where("lastPosition").gt(time));
		return mongoOp.find(q, Passenger.class);
	}

}
