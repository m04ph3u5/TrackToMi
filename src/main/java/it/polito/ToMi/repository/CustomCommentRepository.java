package it.polito.ToMi.repository;

import java.util.List;

import it.polito.ToMi.pojo.Comment;

public interface CustomCommentRepository {
	
	public List<Comment> getLastComments();
	public List<Comment> getCommentsAfterId(String lastId);
}
