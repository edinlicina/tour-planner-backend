package com.tourplanner.backend.repositories;

import com.tourplanner.backend.entities.TourLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourLogRepository extends JpaRepository<TourLogEntity, Long> {
    List<TourLogEntity> findByTourId(Long tourId);
}
