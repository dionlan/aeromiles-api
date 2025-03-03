package com.aeromiles.model.onetwothree.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseDTO {

    private List<FlightOneTwoThreeResponseDTO.FlightDTO> flights;
}
