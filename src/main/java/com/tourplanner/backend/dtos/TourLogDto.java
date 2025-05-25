package com.tourplanner.backend.dtos;

public class TourLogDto {
    private Long id;
    private String dateTime;
    private String comment;
    private String difficulty;
    private float totalDistance;
    private float totalTime;
    private int rating;

    public TourLogDto(Long id, String dateTime, String comment, String difficulty, float totalDistance, float totalTime, int rating) {
        this.id = id;
        this.dateTime = dateTime;
        this.comment = comment;
        this.difficulty = difficulty;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getComment() {
        return comment;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public float getTotalTime() {
        return totalTime;
    }

    public int getRating() {
        return rating;
    }
}
