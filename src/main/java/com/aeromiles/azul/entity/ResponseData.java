package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.TripDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseData {
    private List<TripDTO> trips;
}
