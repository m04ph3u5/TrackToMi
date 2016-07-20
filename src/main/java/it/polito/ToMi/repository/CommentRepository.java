package it.polito.ToMi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.Comment;

public interface CommentRepository extends MongoRepository<Comment, String>, CustomCommentRepository{

	public Comment findById(String id);
}
