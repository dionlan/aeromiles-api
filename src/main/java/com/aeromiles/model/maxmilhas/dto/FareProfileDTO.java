package com.aeromiles.model.maxmilhas.dto;

import lombok.Data;

import java.util.List;

@Data
public class FareProfileDTO {

    private List<String> baggageAllowance;
    private List<String> fareRules;
    private String marketingName;

}
