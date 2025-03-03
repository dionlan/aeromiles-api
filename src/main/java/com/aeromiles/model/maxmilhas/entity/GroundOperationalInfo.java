package com.aeromiles.model.maxmilhas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Embeddable
public class GroundOperationalInfo {

    private String type;

    private String duration;

    private boolean hasChangeOfAirportAfter;

    private String nextBoardingPoint;
}
