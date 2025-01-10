package com.aeromiles.model.maxmilhas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LocationDTO {

    @JsonProperty("locationCode")
    private String locationCode;

    @JsonProperty("dateTime")
    private String dateTime;
}
