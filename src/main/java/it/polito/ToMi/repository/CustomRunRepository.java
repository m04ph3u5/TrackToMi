package it.polito.ToMi.repository;

import java.util.List;

import it.polito.ToMi.pojo.BusStop;
import it.polito.ToMi.pojo.DailyInfo;

public interface CustomRunRepository {
	public void updateRun(String idRun, String idLine, String day, BusStop getIn, BusStop getOut);
	public List<DailyInfo> getDailyInfoTomi();
	public List<DailyInfo> getDailyInfoMito();

}
