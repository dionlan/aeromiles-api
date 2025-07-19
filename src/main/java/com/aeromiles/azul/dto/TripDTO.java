package com.aeromiles.azul.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class TripDTO {
    private String departureStation;
    private String arrivalStation;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime std;
    private String currencyCode;
    private String flightType;
    private String region;
    private FareInformationDTO fareInformation;
    private List<JourneyDTO> journeys;
}
