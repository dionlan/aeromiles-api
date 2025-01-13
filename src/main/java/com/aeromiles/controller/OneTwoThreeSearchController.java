package com.aeromiles.controller;

import com.aeromiles.service.FlightSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class OneTwoThreeSearchController {

    @Autowired
    private FlightSearchService flightSearchService;

    @GetMapping("/simulate-flight-search")
    public String simulateFlightSearch(
            @RequestParam String iataFrom,
            @RequestParam String iataTo,
            @RequestParam String dateOutbound,
            @RequestParam String dateInbound) {
        return flightSearchService.searchFlightOneTwoThree(iataFrom, iataTo, dateOutbound, dateInbound);
    }
}
