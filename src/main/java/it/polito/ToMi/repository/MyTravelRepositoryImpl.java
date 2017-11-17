package it.polito.ToMi.repository;

import com.mongodb.*;
import it.polito.ToMi.pojo.MyTravel;
import it.polito.ToMi.pojo.Travel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;


public class MyTravelRepositoryImpl implements CustomMyTravelRepository {


    @Autowired
    private MongoOperations mongoOp;

    @Override
    public MyTravel findLastByPassenger(String passengerId) {

        Criteria c = new Criteria().andOperator(Criteria.where("passengerId").is(passengerId), Criteria.where("end").exists(false));
        Query q = new Query().addCriteria(c).with(new Sort(Sort.Direction.DESC, "lastUpdate")).limit(1);

        return mongoOp.findOne(q, MyTravel.class);
    }

    @Override
    public List<MyTravel> findMyTravelAfterDate(String id, Date time) {
        Query q = new Query();
        q.addCriteria(Criteria.where("passengerId").is(id)
                .andOperator(Criteria.where("start").gte(time)));
        return mongoOp.find(q, MyTravel.class);
    }
}
