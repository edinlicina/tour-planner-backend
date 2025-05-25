package com.tourplanner.backend.dtos;

public class UpdateTourLogDto {
    private String dateTime;
    private String comment;
    private String difficulty;
    private float totalDistance;
    private float totalTime;
    private int rating;

    public UpdateTourLogDto(String dateTime, String comment, String difficulty, float totalDistance, float totalTime, int rating) {
        this.dateTime = dateTime;
        this.comment = comment;
        this.difficulty = difficulty;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.rating = rating;
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
