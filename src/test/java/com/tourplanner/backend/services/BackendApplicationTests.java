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
	void getTours_ShouldReturnTourDtos() {
		when(tourRepository.findAll()).thenReturn(List.of(new TourEntity()));
		when(tourLogRepository.findByTourId(anyLong())).thenReturn(List.of());

		List<TourDto> result = tourService.getTours();

		assertThat(result).hasSize(1);
	}

	@Test
	void createTour_ShouldSaveTourEntity() {
		CreateTourDto dto = new CreateTourDto("name", "desc", "from", "to", "car", 10.5f, 1.5f);

		tourService.createTour(dto);

		verify(tourRepository).save(any(TourEntity.class));
	}

	@Test
	void deleteTour_ShouldCallDeleteById() {
		tourService.deleteTour(1L);
		verify(tourRepository).deleteById(1L);
	}

	@Test
	void updateTour_TourExists_ShouldUpdateAndReturnDto() {
		TourEntity entity = new TourEntity("old", "desc", "a", "b", "car", 5f, 1f);
		entity.setId(1L);
		when(tourRepository.findById(1L)).thenReturn(Optional.of(entity));
		when(tourLogRepository.findByTourId(1L)).thenReturn(List.of());

		UpdateTourDto dto = new UpdateTourDto("new", "desc", "x", "y", "bike", 15f, 2f);

		TourDto result = tourService.updateTour(1L, dto);

		assertThat(result.getName()).isEqualTo("new");
		verify(tourRepository).save(any(TourEntity.class));
	}

	@Test
	void updateTour_TourNotFound_ShouldThrow404() {
		when(tourRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> tourService.updateTour(1L, new UpdateTourDto()))
				.isInstanceOf(ResponseStatusException.class)
				.hasMessageContaining("404 NOT_FOUND");
	}

	@Test
	void createTourLog_ShouldReturnCreatedDto() {
		TourEntity tour = new TourEntity();
		tour.setId(1L);
		when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

		CreateTourLogDto dto = new CreateTourLogDto("2025-05-27", "Nice trip", "easy", 10f, 1f, 5);
		when(tourLogRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		TourLogDto result = tourService.createTourLog(1L, dto);

		assertThat(result.getComment()).isEqualTo("Nice trip");
		verify(tourLogRepository).save(any(TourLogEntity.class));
	}

	@Test
	void createTourLog_TourNotFound_ShouldThrow404() {
		when(tourRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> tourService.createTourLog(1L, new CreateTourLogDto("2025-05-27", "", "", 0f, 0f, 0)))
				.isInstanceOf(ResponseStatusException.class)
				.hasMessageContaining("404 NOT_FOUND");
	}

	@Test
	void parseDateTimeOrThrow_InvalidDate_ShouldThrowException() {
		assertThatThrownBy(() -> tourService.createTourLog(1L,
				new CreateTourLogDto("invalid-date", "", "", 0f, 0f, 0)))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	void generateAllToursPdf_ShouldReturnPdfBytes() {
		when(tourRepository.findAll()).thenReturn(List.of(new TourEntity()));
		when(tourLogRepository.findByTourId(anyLong())).thenReturn(List.of());

		byte[] pdf = tourService.generateAllToursPdf();

		assertThat(pdf).isNotEmpty();
	}

	@Test
	void getTourLogs_ShouldReturnTourLogDtos() {
		TourLogEntity log = new TourLogEntity();
		log.setComment("Test log");
		log.setRating(4);
		log.setDateTime(LocalDateTime.now());
		when(tourLogRepository.findByTourId(1L)).thenReturn(List.of(log));

		List<TourLogDto> result = tourService.getTourLogs(1L);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getComment()).isEqualTo("Test log");
	}
}
