package com.tourplanner.backend.controllers;

import com.tourplanner.backend.dtos.*;
import com.tourplanner.backend.services.TourService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tours")
public class TourController {
    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping
    public List<TourDto> getTours() {
        return tourService.getTours();
    }

    @PostMapping
    public void createTour(@RequestBody CreateTourDto dto) {
        tourService.createTour(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteTour(@PathVariable Long id) {
        tourService.deleteTour(id);


    }

    @PutMapping("/{id}")
    public TourDto updateTour(@PathVariable Long id, @RequestBody UpdateTourDto dto) {
        return tourService.updateTour(id, dto);

    }

    //TourLogs
    @PostMapping("/{tourId}/tour-logs")
    public TourLogDto createTourLog(@PathVariable Long tourId, @RequestBody CreateTourLogDto dto) {
        return tourService.createTourLog(tourId, dto);
    }
    @DeleteMapping("/{tourId}/tour-logs/{tourLogId}")
    public void deleteTourLog(@PathVariable Long tourId, @PathVariable Long tourLogId){
        tourService.deleteTourLog(tourId, tourLogId);
    }
    @GetMapping("/{tourId}/tour-logs")
    public List<TourLogDto> getTourLogs(@PathVariable Long tourId){
        return tourService.getTourLogs(tourId);
    }
    @PutMapping("/{tourId}/tour-logs/{tourLogId}")
    public TourLogDto updateTourLog(@PathVariable Long tourId, @PathVariable Long tourLogId, @RequestBody UpdateTourLogDto dto){
        return tourService.updateTourLog(tourId, tourLogId, dto);
    }
    @GetMapping("/report")
    public ResponseEntity<byte[]> exportAllToursPdf() {
        byte[] pdf = tourService.generateAllToursPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename("all-tours.pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

}
