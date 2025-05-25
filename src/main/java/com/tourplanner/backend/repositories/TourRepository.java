package com.tourplanner.backend.repositories;

import com.tourplanner.backend.entities.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<TourEntity, Long> {
}
