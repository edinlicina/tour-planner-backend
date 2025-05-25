package com.tourplanner.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tour_logs")
public class TourLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;

    private String comment;

    private String difficulty;

    private float totalDistance;

    private float totalTime;

    private int rating;

    // Optional: Link to the parent tour
    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private TourEntity tour;

    // --- Constructors ---

    public TourLogEntity() {}

    public TourLogEntity(LocalDateTime dateTime, String comment, String difficulty,
                   float totalDistance, float totalTime, int rating, TourEntity tour) {
        this.dateTime = dateTime;
        this.comment = comment;
        this.difficulty = difficulty;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.rating = rating;
        this.tour = tour;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public float getTotalDistance() { return totalDistance; }
    public void setTotalDistance(float totalDistance) { this.totalDistance = totalDistance; }

    public float getTotalTime() { return totalTime; }
    public void setTotalTime(float totalTime) { this.totalTime = totalTime; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public TourEntity getTour() { return tour; }
    public void setTour(TourEntity tour) { this.tour = tour; }
}