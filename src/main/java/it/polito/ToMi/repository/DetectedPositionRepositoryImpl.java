package it.polito.ToMi.repository;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import it.polito.ToMi.pojo.DetectedPosition;
import it.polito.ToMi.pojo.Position;

public class DetectedPositionRepositoryImpl implements CustomDetectedPositionRepository {

  @Autowired
  MongoOperations mongoOp;

  @Value("${app.num.userhistory}")
  private int userHistory;

  /*
   * (non-Javadoc)
   * 
   * @see it.polito.ToMi.repository.CustomDetectedPositionRepository#
   * findByUserIdAfterDate(java.util.Date)
   */
  @Override
  public List<DetectedPosition> findByUserIdAfterDate(String userId, Date lastUpdate, Sort sort) {
    Query q = new Query();
    q.addCriteria(Criteria.where("timestamp").gte(lastUpdate).andOperator(Criteria.where("userId").is(userId)));
    q.with(sort);
    return mongoOp.find(q, DetectedPosition.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * it.polito.ToMi.repository.CustomDetectedPositionRepository#findByUserId(
   * java.lang.String, org.springframework.data.domain.Sort)
   */
  @Override
  public List<DetectedPosition> findByUserId(String userId, Sort sort) {
    Query q = new Query();
    q.addCriteria(Criteria.where("userId").is(userId));
    q.with(sort);
    return mongoOp.find(q, DetectedPosition.class);
  }

  @Override
  public List<DetectedPosition> findByUserIdWithUserInteractionTrue(String userId) {
    Query q = new Query();
    q.addCriteria(Criteria.where("userId").is(userId).andOperator(Criteria.where("userInteraction").is(true)));
    q.with(new Sort(Sort.Direction.DESC, "timestamp"));
    q.limit(userHistory);
    return mongoOp.find(q, DetectedPosition.class);
  }

  @Override
  public List<DetectedPosition> findAllNonEmptyPosition(Date since) {
    Query q = new Query();
    q.addCriteria(Criteria.where("position.lat").ne(Position.FAKE_VALUE)
        .andOperator(Criteria.where("timestamp").gte(since)));
    return mongoOp.find(q, DetectedPosition.class);
  }

}
