package it.polito.ToMi.repository;

import java.util.Date;
import java.util.List;

import it.polito.ToMi.pojo.Comment;

public interface CustomCommentRepository {

  public List<Comment> getLastComments();
  //	public List<Comment> getCommentsAfterId(String lastId);
  public List<Comment> getLastCommentsByCategory(String category);
  public List<Comment> getLastCommentBeforeDate(Date time);
  public List<Comment> getLastCommentByCategoryBeforeDate(Date time, String category);

}
