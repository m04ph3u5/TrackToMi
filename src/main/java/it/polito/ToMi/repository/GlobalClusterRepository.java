/**
 * 
 */
package it.polito.ToMi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.clustering.GlobalCluster;

/**
 * @author m04ph3u5
 *
 */
public interface GlobalClusterRepository extends MongoRepository<GlobalCluster, String>{
	
}
