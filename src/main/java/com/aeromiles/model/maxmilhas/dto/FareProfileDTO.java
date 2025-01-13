package com.aeromiles.model.maxmilhas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FareProfileDTO {

    @JsonProperty("marketingName")
    private String marketingName;

}
