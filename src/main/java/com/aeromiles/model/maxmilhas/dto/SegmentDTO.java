package com.aeromiles.model.maxmilhas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SegmentDTO {

    @JsonProperty("operatingFlightNumber")
    private String operatingFlightNumber;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("stopQuantity")
    private Integer stopQuantity;

    @JsonProperty("id")
    private String id;

    @JsonProperty("cabin")
    private String cabin;

    @JsonProperty("bookingClass")
    private String bookingClass;

    @JsonProperty("departure")
    private LocationDTO departure;

    @JsonProperty("arrival")
    private LocationDTO arrival;
}
