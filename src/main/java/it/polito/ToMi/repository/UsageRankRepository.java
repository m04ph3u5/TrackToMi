package it.polito.ToMi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.UsageRank;

public interface UsageRankRepository extends MongoRepository<UsageRank, String>, CustomUsageRankRepository {

  public UsageRank findByPassengerId(String passengerId);
}
