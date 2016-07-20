package it.polito.ToMi.repository;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import it.polito.ToMi.pojo.DetectedPosition;

public class DetectedPositionRepositoryImpl implements CustomDetectedPositionRepository{

	@Autowired
	MongoOperations mongoOp;
	
	@Override
	public List<DetectedPosition> getMyPositions(String userId, long start, long end) {
		Query q = new Query();
		q.addCriteria(Criteria.where("timestamp").gte(start).
						andOperator(Criteria.where("timestamp").lte(end)).
						andOperator(Criteria.where("userId").is(userId)));
		return mongoOp.find(q, DetectedPosition.class);
	}

	/* (non-Javadoc)
	 * @see it.polito.ToMi.repository.CustomDetectedPositionRepository#findByUserIdAfterDate(java.util.Date)
	 */
	@Override
	public List<DetectedPosition> findByUserIdAfterDate(String userId, Date lastUpdate, Sort sort) {
		Query q = new Query();
		q.addCriteria(Criteria.where("timestamp").gte(lastUpdate).
						andOperator(Criteria.where("userId").is(userId)));
		q.with(sort);
		return mongoOp.find(q, DetectedPosition.class);
	}

	/* (non-Javadoc)
	 * @see it.polito.ToMi.repository.CustomDetectedPositionRepository#findByUserId(java.lang.String, org.springframework.data.domain.Sort)
	 */
	@Override
	public List<DetectedPosition> findByUserId(String userId, Sort sort) {
		Query q = new Query();
		q.addCriteria(Criteria.where("userId").is(userId));
		q.with(sort);
		return mongoOp.find(q, DetectedPosition.class);
	}

}
