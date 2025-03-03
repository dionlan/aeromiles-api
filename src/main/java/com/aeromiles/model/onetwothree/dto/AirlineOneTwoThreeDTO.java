package com.aeromiles.model.onetwothree.dto;

import com.aeromiles.model.onetwothree.AirlineOneTwoThree;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirlineOneTwoThreeDTO {

    @JsonProperty("CODE")
    private String code;

    @JsonProperty("NAME")
    private String name;

    @JsonProperty("CODE_CIA")
    private String codeCia;

    private String airlineLogo;

    public static AirlineOneTwoThree toEntity(AirlineOneTwoThreeDTO dto) {
        AirlineOneTwoThree airline = new AirlineOneTwoThree();
        airline.setCode(dto.getCode());
        airline.setName(dto.getName().toUpperCase());
        airline.setCodeCia(dto.getCodeCia());
        airline.setAirlineLogo(getAirlineLogo(dto.getName()));
        return airline;
    }

    public static AirlineOneTwoThreeDTO toDTO(AirlineOneTwoThree entity) {
        AirlineOneTwoThreeDTO dto = new AirlineOneTwoThreeDTO();
        dto.setCode(entity.getCode());
        dto.setName(entity.getName().toUpperCase());
        dto.setCodeCia(entity.getCodeCia());
        dto.setAirlineLogo(getAirlineLogo(entity.getName()));
        return dto;
    }

    private static String getAirlineLogo(String name){
        String logo;
        if(name.equalsIgnoreCase("TAM")){
            logo = "https://content.r9cdn.net/rimg/provider-logos/airlines/v/LA.png?crop=false&width=108&height=92&fallback=default1.png";
        }else if(name.equalsIgnoreCase("GOL")){
            logo = "https://content.r9cdn.net/rimg/provider-logos/airlines/v/G3.png?crop=false&width=108&height=92&fallback=default1.png";
        }else if(name.equalsIgnoreCase("AZUL")){
            logo = "https://content.r9cdn.net/rimg/provider-logos/airlines/v/AD.png?crop=false&width=108&height=92&fallback=default1.png";
        }else{
            logo = "https://content.r9cdn.net/rimg/provider-logos/airlines/v/LAf.png?crop=false&width=108&height=92&fallback=default1.png";
        }
        return logo;
    }
}
