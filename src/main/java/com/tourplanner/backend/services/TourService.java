package com.tourplanner.backend.services;

import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.tourplanner.backend.dtos.*;
import com.tourplanner.backend.entities.TourEntity;
import com.tourplanner.backend.entities.TourLogEntity;
import com.tourplanner.backend.repositories.TourLogRepository;
import com.tourplanner.backend.repositories.TourRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class TourService {
    private static final Logger logger = LogManager.getLogger(TourService.class);

    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;

    public TourService(TourRepository tourRepository, TourLogRepository tourLogRepository) {
        this.tourRepository = tourRepository;
        this.tourLogRepository = tourLogRepository;
    }

    public List<TourDto> getTours() {
        logger.info("Getting Tours");
        List<TourEntity> tours = tourRepository.findAll();
        return toDtos(tours);
    }

    public void createTour(CreateTourDto dto) {
        logger.info("Creating a new tour");
        TourEntity tourEntity = new TourEntity(
                dto.getName(),
                dto.getDescription(),
                dto.getFrom(),
                dto.getTo(),
                dto.getTransportType(),
                dto.getDistance(),
                dto.getEstTime()
        );
        tourRepository.save(tourEntity);
        logger.info("Tour created");
    }

    public void deleteTour(long id) {
        logger.info("Deleting Tour with id " + id);
        tourRepository.deleteById(id);
        logger.info("Deleted Tour with id " + id);
    }

    public TourDto updateTour(long id, UpdateTourDto dto) {
        logger.info("Update a Tour with id " + id);
        TourEntity tourEntity = tourRepository.findById(id).orElseThrow(() -> {
            logger.error("Tour with id " + id + " was not found in the database");
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour not found");
        });
        tourEntity.setDistance(dto.getDistance());
        tourEntity.setName(dto.getName());
        tourEntity.setEndLocation(dto.getTo());
        tourEntity.setStartLocation(dto.getFrom());
        tourEntity.setTransportType(dto.getTransportType());
        tourEntity.setDescription(dto.getDescription());
        tourEntity.setEstimatedTime(dto.getEstTime());
        tourRepository.save(tourEntity);
        logger.info("Updated Tour with id "+ id);
        return toDto(tourEntity);

    }

    private List<TourDto> toDtos(List<TourEntity> entities) {
        return entities.stream().map(entity -> toDto(entity)).toList();
    }

    private List<TourLogDto> toTourLogDtos(List<TourLogEntity> entities){
        return entities.stream().map(entity->toTourLogDto(entity)).toList();
    }

    private TourLogDto toTourLogDto(TourLogEntity entity) {
        return new TourLogDto(
                entity.getId(),
                entity.getDateTime().toString(),
                entity.getComment(),
                entity.getDifficulty(),
                entity.getTotalDistance(),
                entity.getTotalTime(),
                entity.getRating()
        );
    }

    private TourDto toDto(TourEntity entity) {
        List<TourLogEntity> tourLogEntities = tourLogRepository.findByTourId(entity.getId());
        float sumOfRatings = 0;
        for(TourLogEntity tourLogEntity: tourLogEntities){
            sumOfRatings+=tourLogEntity.getRating();
        }
        float avgRating = 0;
        if(!tourLogEntities.isEmpty()){
            avgRating=sumOfRatings/tourLogEntities.size();
        }
        String popularity = "udefined";
        if(tourLogEntities.size()>1){
            popularity = "low";
        }
        if(tourLogEntities.size()>=5){
            popularity = "medium";
        }
        if(tourLogEntities.size()>=10){
            popularity = "popular";
        }
        return new TourDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStartLocation(),
                entity.getEndLocation(),
                entity.getTransportType(),
                entity.getDistance(),
                entity.getEstimatedTime(),
                avgRating,
                popularity

        );


    }

    // TourLogsLogic

    private LocalDateTime parseDateTimeOrThrow(String input) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(input, formatter);
            return localDate.atStartOfDay();
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format. Expected: yyyy-MM-dd received "+ input );
            throw new IllegalArgumentException("Invalid date format. Expected: yyyy-MM-dd");
        }
    }

    public TourLogDto createTourLog(Long tourId, CreateTourLogDto dto) {
        logger.info("Creating Tour log for Tour with id " + tourId);
        TourEntity tourEntity = tourRepository.findById(tourId).orElseThrow(() -> {
            logger.error("Tour with tourId " + tourId + " was not found in the database");
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour not found");
        });
        LocalDateTime dateTime = parseDateTimeOrThrow(dto.getDateTime());
        TourLogEntity tourLogEntity = new TourLogEntity(
                dateTime,
                dto.getComment(),
                dto.getDifficulty(),
                dto.getTotalDistance(),
                dto.getTotalTime(),
                dto.getRating(),
                tourEntity
        );
        TourLogEntity created = tourLogRepository.save(tourLogEntity);
        logger.info("Tour log for Tour with id " + tourId + " created");
        return toTourLogDto(created);
    }

    public void deleteTourLog(Long tourId, Long tourLogId) {
        logger.info("Deleting Tour log with id " + tourLogId + " for tour with id " + tourId);
        TourLogEntity tourLogEntity = tourLogRepository.findById(tourLogId).orElseThrow(() -> {
           logger.error("Tour Log with id "+ tourLogId +"was not found in Database" );
           return new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour Log not found");
       });
       if(tourLogEntity.getTour().getId().longValue() != tourId.longValue()){
           logger.error("Tour Id "+ tourLogEntity.getTour().getId() + " of Tour Log doesn't match given Tour Id "+tourId);
           throw new ResponseStatusException(
                   HttpStatus.NOT_FOUND,
                   "Tour Id of Tour Log doesn't match given Tour Id"
           );
       }
        tourLogRepository.deleteById(tourLogId);
        logger.info("Tour log with id " + tourLogId + " for tour with id " + tourId + " deleted");
    }

    public List<TourLogDto> getTourLogs(Long tourId) {
        logger.info("Getting Tour logs for tour with id " + tourId);
        List<TourLogEntity> tourLogEntities = tourLogRepository.findByTourId(tourId);
        return toTourLogDtos(tourLogEntities);
    }

    public TourLogDto updateTourLog(Long tourId, Long tourLogId, UpdateTourLogDto dto) {
        logger.info("Updating Tour log with Id "+ tourLogId +" for Tour with Tour with Id "+ tourId);
        TourLogEntity tourLogEntity = tourLogRepository.findById(tourLogId).orElseThrow(() -> {
            logger.error("Tour Log with id "+ tourLogId +"was not found in Database" );
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour Log not found");
        });
        if(tourLogEntity.getTour().getId().longValue() != tourId.longValue()){
            logger.error("Tour Id "+ tourLogEntity.getTour().getId() + " of Tour Log doesn't match given Tour Id "+tourId);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Tour Id of Tour Log doesn't match given Tour Id"
            );
        }
        LocalDateTime dateTime = parseDateTimeOrThrow(dto.getDateTime());
        tourLogEntity.setComment(dto.getComment());
        tourLogEntity.setDateTime(dateTime);
        tourLogEntity.setDifficulty(dto.getDifficulty());
        tourLogEntity.setTotalDistance(dto.getTotalDistance());
        tourLogEntity.setTotalTime(dto.getTotalTime());
        tourLogEntity.setRating(dto.getRating());
        tourLogRepository.save(tourLogEntity);
        logger.info("Tour log with Id "+ tourLogId +" for Tour with Tour with Id "+ tourId + " updated");
        return toTourLogDto(tourLogEntity);
    }

    public byte[] generateAllToursPdf() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<TourEntity> tours = tourRepository.findAll();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("All Tours", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            document.add(new Paragraph(" ")); // blank line

            for (TourEntity tour : tours) {
                List<TourLogEntity> tourLogEntities = tourLogRepository.findByTourId(tour.getId());
                document.add(new Paragraph("Name: " + tour.getName()));
                document.add(new Paragraph("Description: " + tour.getDescription()));
                document.add(new Paragraph("From: " + tour.getStartLocation()));
                document.add(new Paragraph("To: " + tour.getEndLocation()));
                document.add(new Paragraph("Transport: " + tour.getTransportType()));
                document.add(new Paragraph("Distance: " + tour.getDistance() + " km"));
                document.add(new Paragraph("Estimated Time: " + tour.getEstimatedTime() + " h"));
                document.add(new Paragraph("----------------------------------------"));
                for(TourLogEntity tourLogEntity: tourLogEntities){
                    document.add(new Paragraph("Comment: " + tourLogEntity.getComment()));
                    document.add(new Paragraph("Date/Time: " + tourLogEntity.getDateTime()));
                    document.add(new Paragraph("Difficulty: " + tourLogEntity.getDifficulty()));
                    document.add(new Paragraph("Total Distance: " + tourLogEntity.getTotalDistance()));
                    document.add(new Paragraph("Total Time: " + tourLogEntity.getTotalTime()));
                    document.add(new Paragraph("Rating: " + tourLogEntity.getRating()));
                    document.add(new Paragraph("----------------------------------------"));

                }
                document.add(new Paragraph("----------------------------------------"));
                document.add(new Paragraph("----------------------------------------"));

            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate tour PDF", e);
        }
    }
}
