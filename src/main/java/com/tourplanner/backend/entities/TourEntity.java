package com.tourplanner.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tours")
public class TourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "start_location")
    private String startLocation;

    @Column(name = "end_location")
    private String endLocation;

    @Column(name = "transport_type")
    private String transportType;

    private float distance;

    @Column(name = "estimated_time")
    private float estimatedTime;

    public TourEntity() {
    }

    public TourEntity(String name, String description, String startLocation, String endLocation,
                      String transportType, float distance, float estimatedTime) {
        this.name = name;
        this.description = description;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.transportType = transportType;
        this.distance = distance;
        this.estimatedTime = estimatedTime;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getDistance() {
        return distance;
    }

    public float getEstimatedTime() {
        return estimatedTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setEstimatedTime(float estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}