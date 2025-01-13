package com.aeromiles.model.onetwothree.dto;

import com.aeromiles.model.onetwothree.AirlineOneTwoThree;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AirlineOneTwoThreeDTO {

    @JsonProperty("CODE")
    private String code;

    @JsonProperty("NAME")
    private String name;

    @JsonProperty("CODE_CIA")
    private String codeCia;

    public static AirlineOneTwoThree toEntity(AirlineOneTwoThreeDTO dto) {
        AirlineOneTwoThree airline = new AirlineOneTwoThree();
        airline.setCode(dto.getCode());
        airline.setName(dto.getName());
        airline.setCodeCia(dto.getCodeCia());
        return airline;
    }

}
