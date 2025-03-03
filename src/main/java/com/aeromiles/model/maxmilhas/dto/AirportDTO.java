package com.aeromiles.model.maxmilhas.dto;

import com.aeromiles.model.Airport;
import lombok.Data;

@Data
public class AirportDTO {

    private String code;
    private String name;
    private String state;
    private String city;
    private String country;

    public static Airport toEntity(AirportDTO dto){
        Airport airport = new Airport();
        airport.setCode(dto.getCode());
        airport.setName(dto.getName().toUpperCase());
        airport.setState(dto.getState());
        airport.setCity(dto.getCity());
        airport.setCountry(dto.getCountry());
        return  airport;
    }

    public static AirportDTO toDTO(Airport airport){
        AirportDTO dto = new AirportDTO();
        dto.setCode(airport.getCode());
        dto.setName(airport.getName().toUpperCase());
        dto.setState(airport.getState());
        dto.setCity(airport.getCity());
        dto.setCountry(airport.getCountry());
        return dto;
    }
}
