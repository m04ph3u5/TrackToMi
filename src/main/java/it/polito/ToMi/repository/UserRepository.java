package it.polito.ToMi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.User;

public interface UserRepository extends MongoRepository<User, String>{

	public User findById(String id);
	public User findByUsername(String username);
	
	
}
