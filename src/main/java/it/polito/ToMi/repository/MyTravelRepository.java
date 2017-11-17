package it.polito.ToMi.repository;

import it.polito.ToMi.pojo.MyTravel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MyTravelRepository extends MongoRepository<MyTravel, String>,CustomMyTravelRepository{

    MyTravel findById(String id);

    List<MyTravel> findByIdAndPassengerId(String id, String passengerId);


}
