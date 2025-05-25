package com.tourplanner.backend.services;

import com.tourplanner.backend.dtos.*;
import com.tourplanner.backend.entities.TourEntity;
import com.tourplanner.backend.entities.TourLogEntity;
import com.tourplanner.backend.repositories.TourLogRepository;
import com.tourplanner.backend.repositories.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TourServiceTest {

	@Mock
	private TourRepository tourRepository;

	@Mock
	private TourLogRepository tourLogRepository;

	@InjectMocks
	private TourService tourService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createTour_withValidDto_savesTour() {
		CreateTourDto dto = new CreateTourDto("Tour Name", "Description", "Vienna, Austria", "Graz, Austria", "driving-car", 100f, 2f);
		tourService.createTour(dto);
		verify(tourRepository).save(any(TourEntity.class));
	}

	@Test
	void createTour_withInvalidTransportType_throwsBadRequest() {
		CreateTourDto dto = new CreateTourDto("Tour", "Desc", "Vienna, Austria", "Graz, Austria", "flying-dragon", 100f, 2f);
		assertThatThrownBy(() -> tourService.createTour(dto)).isInstanceOf(ResponseStatusException.class);
	}

	@Test
	void deleteTour_deletesById() {
		tourService.deleteTour(1L);
		verify(tourRepository).deleteById(1L);
	}

	@Test
	void updateTour_withValidDto_updatesTour() {
		TourEntity entity = new TourEntity("Old", "Desc", "Vienna, Austria", "Berlin, Germany", "driving-car", 50f, 1f);
		entity.setId(1L);
		when(tourRepository.findById(1L)).thenReturn(Optional.of(entity));
		when(tourLogRepository.findByTourId(1L)).thenReturn(List.of());

		UpdateTourDto dto = new UpdateTourDto("New", "Desc", "Vienna, Austria", "Berlin, Germany", "cycling-electric", 70f, 3f);
		TourDto result = tourService.updateTour(1L, dto);

		assertThat(result.getName()).isEqualTo("New");
		verify(tourRepository).save(any());
	}

	@Test
	void updateTour_withInvalidLocation_throwsBadRequest() {
		TourEntity entity = new TourEntity("Old", "Desc", "A", "B", "driving-car", 50f, 1f);
		entity.setId(1L);
		when(tourRepository.findById(1L)).thenReturn(Optional.of(entity));
		UpdateTourDto dto = new UpdateTourDto("Name", "Desc", "Invalid", "City", "cycling-mountain", 50f, 1f);

		assertThatThrownBy(() -> tourService.updateTour(1L, dto)).isInstanceOf(ResponseStatusException.class);
	}

	@Test
	void createTourLog_withValidData_returnsDto() {
		CreateTourLogDto dto = new CreateTourLogDto("2025-05-25", "Comment", "Medium", 50f, 2f, 4);
		TourEntity tour = new TourEntity();
		tour.setId(1L);
		when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
		when(tourLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		TourLogDto result = tourService.createTourLog(1L, dto);

		assertThat(result.getComment()).isEqualTo("Comment");
		verify(tourLogRepository).save(any());
	}

	@Test
	void createTourLog_withInvalidRating_throwsBadRequest() {
		CreateTourLogDto dto = new CreateTourLogDto("2025-05-25", "Text", "Medium", 10f, 1f, 6);
		assertThatThrownBy(() -> tourService.createTourLog(1L, dto)).isInstanceOf(ResponseStatusException.class);
	}

	@Test
	void updateTourLog_withValidDto_returnsUpdatedDto() {
		UpdateTourLogDto dto = new UpdateTourLogDto("2025-10-28", "Hard", "Hard", 2f, 4, 4);
		TourLogEntity log = new TourLogEntity();
		log.setTour(new TourEntity());
		log.getTour().setId(1L);
		log.setId(2L);

		when(tourLogRepository.findById(2L)).thenReturn(Optional.of(log));

		TourLogDto result = tourService.updateTourLog(1L, 2L, dto);

		assertThat(result.getComment()).isEqualTo("Hard");
		verify(tourLogRepository).save(any());
	}

	@Test
	void getTours_returnsListOfDtos() {
		when(tourRepository.findAll()).thenReturn(List.of(new TourEntity()));
		when(tourLogRepository.findByTourId(anyLong())).thenReturn(List.of());

		List<TourDto> tours = tourService.getTours();
		assertThat(tours).isNotEmpty();
	}

	@Test
	void generateAllToursPdf_returnsByteArray() {
		TourEntity tour = new TourEntity("Tour", "Desc", "A", "B", "driving-car", 20f, 1f);
		tour.setId(1L);
		when(tourRepository.findAll()).thenReturn(List.of(tour));
		when(tourLogRepository.findByTourId(1L)).thenReturn(Collections.emptyList());

		byte[] pdf = tourService.generateAllToursPdf();
		assertThat(pdf).isNotEmpty();
	}
}
