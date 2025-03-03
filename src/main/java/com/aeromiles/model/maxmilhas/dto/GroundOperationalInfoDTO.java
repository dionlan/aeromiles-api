package com.aeromiles.model.maxmilhas.dto;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class GroundOperationalInfoDTO {

    private String type;

    private String duration;

    private boolean hasChangeOfAirportAfter;

    private String nextBoardingPoint;
}
