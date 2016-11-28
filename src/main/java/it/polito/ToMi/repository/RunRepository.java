package it.polito.ToMi.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.polito.ToMi.pojo.Run;

public interface RunRepository extends MongoRepository<Run, String>, CustomRunRepository{

	public Run findByIdRunAndIdLineAndDay(String idRun, String idLine, String day);

	public List<Run> findByTimestampAndDirection(long time, boolean direction);

}
