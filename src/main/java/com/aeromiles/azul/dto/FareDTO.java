package com.aeromiles.azul.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FareDTO {
    private String key;
    private boolean available;
    private String classOfService;
    private String fareSellKey;
    private List<PaxPointsDTO> paxPoints;
    private ProductClassDTO productClass;
}
