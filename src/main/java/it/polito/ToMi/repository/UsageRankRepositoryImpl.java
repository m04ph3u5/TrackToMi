package it.polito.ToMi.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import it.polito.ToMi.pojo.UsageRank;

public class UsageRankRepositoryImpl implements CustomUsageRankRepository{
  
  @Value("${lottery.winner}")
  private int lotteryWinners;

  @Autowired
  private MongoOperations mongoOp;
  
  @Override
  public List<UsageRank> findTop() {
    Query q = new Query();
    q.addCriteria(Criteria.where("refused").is(false));
    q.with(new Sort(Sort.Direction.ASC,"timestamp"));
    q.limit(lotteryWinners);
    return mongoOp.find(q, UsageRank.class);
  }

  @Override
  public boolean isLotteryClosed() {
    Query q = new Query();
    q.addCriteria(Criteria.where("accepted").is(true));
    q.with(new Sort(Sort.Direction.ASC,"timestamp"));
    q.limit(lotteryWinners);
    List<UsageRank> list =  mongoOp.find(q, UsageRank.class);
    if(list!=null && list.size()==lotteryWinners){
      return true;
    } else {
      return false;
    }
  }

}
