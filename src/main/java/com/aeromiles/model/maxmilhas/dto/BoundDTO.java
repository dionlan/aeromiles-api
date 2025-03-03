package com.aeromiles.model.maxmilhas.dto;

import lombok.Data;

import java.util.List;

@Data
public class BoundDTO {

    private String carrier;

    private String validatedBy;

    private String duration;

    private int daysDifference;

    private LocationDTO departure;

    private LocationDTO arrival;

    private boolean hasCheckedBags;

    private boolean hasCarryOnBags;

    private int totalStops;

    private List<SegmentDTO> segments;

    private FareProfileDTO fareProfile;

}
