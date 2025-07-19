package com.aeromiles.azul.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JourneyDTO {
    private String journeyKey;
    private String journeySellKey;
    private JourneysIdentifierDTO identifier;
    private List<FareDTO> fares;
    private List<SegmentsDTO> segments;
}
