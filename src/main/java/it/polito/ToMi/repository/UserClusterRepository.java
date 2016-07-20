/**
 * 
 */
package it.polito.ToMi.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.clustering.UserCluster;

/**
 * @author m04ph3u5
 *
 */
public interface UserClusterRepository extends MongoRepository<UserCluster, String>{

	public long deleteByUserId(String userId);

	/**
	 * @param userId
	 * @return
	 */
	public List<UserCluster> findByUserId(String userId);
}
