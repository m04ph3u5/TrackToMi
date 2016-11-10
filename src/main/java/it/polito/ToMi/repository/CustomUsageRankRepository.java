package it.polito.ToMi.repository;

import java.util.List;

import it.polito.ToMi.pojo.UsageRank;

public interface CustomUsageRankRepository {

  public List<UsageRank> findTop();
  public boolean isLotteryClosed();
}
