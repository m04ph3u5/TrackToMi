package it.polito.ToMi.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;

import it.polito.ToMi.pojo.DetectedPosition;

public interface CustomDetectedPositionRepository {

	/**
	 * @param lastUpdate
	 * @param sort 
	 * @return
	 */
	List<DetectedPosition> findByUserIdAfterDate(String userId, Date lastUpdate, Sort sort);
	List<DetectedPosition> findByUserId(String userId, Sort sort);
	List<DetectedPosition> findByUserIdWithUserInteractionTrue(String userId);
	List<DetectedPosition> findAllNonEmptyPosition(Date since);
}
