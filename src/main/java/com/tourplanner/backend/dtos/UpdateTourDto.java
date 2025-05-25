package com.tourplanner.backend.dtos;

public class UpdateTourDto {
    private String name;
    private String description;
    private String from;
    private String to;
    private String transportType;
    private float distance;
    private float estTime;

    public UpdateTourDto() {
    }

    public UpdateTourDto(String name, String description, String from, String to, String transportType, float distance, float estTime) {
        this.name = name;
        this.description = description;
        this.from = from;
        this.to = to;
        this.transportType = transportType;
        this.distance = distance;
        this.estTime = estTime;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getTransportType() {
        return transportType;
    }

    public float getDistance() {
        return distance;
    }

    public float getEstTime() {
        return estTime;
    }
}
