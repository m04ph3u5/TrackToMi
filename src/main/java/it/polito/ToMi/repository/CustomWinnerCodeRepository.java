package it.polito.ToMi.repository;

import it.polito.ToMi.pojo.WinnerCode;

public interface CustomWinnerCodeRepository {

  public WinnerCode getNotUsedCode();
}
