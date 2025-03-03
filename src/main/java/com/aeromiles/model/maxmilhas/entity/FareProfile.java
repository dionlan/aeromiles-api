package com.aeromiles.model.maxmilhas.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.List;

@Data
@Embeddable
public class FareProfile {

    private List<String> baggageAllowance;
    private List<String> fareRules;
    private String marketingName;
}
