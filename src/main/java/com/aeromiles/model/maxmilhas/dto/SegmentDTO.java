package com.aeromiles.model.maxmilhas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SegmentDTO {

    @JsonProperty("id")
    private String idSegment;

    private String marketingAirlineCode;

    private String operatingAirlineCode;

    private String marketingFlightNumber;

    private String operatingFlightNumber;

    private LocationDTO departure;

    private LocationDTO arrival;

    private String duration;

    private int stopQuantity;

    //private String equipment;

    private GroundOperationalInfoDTO groundOperationalInfo;

    private String cabin;

    private String bookingClass;

    private String fareClass;

    private String fareBasis;
}
