package com.aeromiles.model.maxmilhas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BoundDTO {

    @JsonProperty("segments")
    private List<SegmentDTO> segments;

    @JsonProperty("fareProfile")
    private FareProfileDTO fareProfile;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("departure")
    private LocationDTO departure;

    @JsonProperty("arrival")
    private LocationDTO arrival;

    @JsonProperty("totalStops")
    private Integer totalStops;

}
