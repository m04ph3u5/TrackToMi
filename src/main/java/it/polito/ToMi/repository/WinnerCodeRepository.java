package it.polito.ToMi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.WinnerCode;

public interface WinnerCodeRepository extends MongoRepository<WinnerCode, String>, CustomWinnerCodeRepository{


}
