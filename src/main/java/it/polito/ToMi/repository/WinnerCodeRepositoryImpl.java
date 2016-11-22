package it.polito.ToMi.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import it.polito.ToMi.pojo.WinnerCode;

public class WinnerCodeRepositoryImpl implements CustomWinnerCodeRepository{

  @Autowired
  private MongoOperations mongoOp;

  @Override
  public WinnerCode getNotUsedCode() {
    Query q = new Query();
    q.addCriteria(Criteria.where("used").is(false));
    Update u = new Update();
    u.set("used", true);
    
    return mongoOp.findAndModify(q, u, WinnerCode.class);
  }
  
  
}
