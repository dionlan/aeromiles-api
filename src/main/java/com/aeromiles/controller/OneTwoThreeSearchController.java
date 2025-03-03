package com.aeromiles.controller;

import com.aeromiles.model.onetwothree.dto.FlightOneTwoThreeDTO;
import com.aeromiles.model.onetwothree.dto.FlightOneTwoThreeResponseDTO;
import com.aeromiles.model.onetwothree.dto.ResponseDTO;
import com.aeromiles.service.FlightSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class OneTwoThreeSearchController {

    @Autowired
    private FlightSearchService flightSearchService;

    @GetMapping("/simulate-flight-search")
    public String simulateFlightSearch(
            @RequestParam String codeFrom,
            @RequestParam String codeTo,
            @RequestParam String dateOutbound,
            @RequestParam String dateInbound) {
        return flightSearchService.searchFlightOneTwoThree(codeFrom, codeTo, dateOutbound, dateInbound);
    }

    @GetMapping("/search123")
    public ResponseDTO search(
            @RequestParam String fromCity,
            @RequestParam String toCity,
            @RequestParam String departureDate,
            @RequestParam boolean consultaExterna) {
        return flightSearchService.search(fromCity, toCity, departureDate, consultaExterna);
    }
}
