package com.aeromiles.controller;

import com.aeromiles.model.FlightFilter;
import com.aeromiles.service.FlightSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightSearchController {

    @Autowired
    private FlightSearchService flightSearchService;

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchFlights() throws IOException {
        String response = flightSearchService.searchAirlines();
        String searchId = flightSearchService.getSearchIdFromResponse(response);
        List<String> airlines = flightSearchService.parseAirlines(response);

        List<String> allFlights = new ArrayList<>();

        for (String airline : airlines) {
            String flight = flightSearchService.searchFlightsByAirline(airline, searchId);
            allFlights.add(flight);
        }

        return ResponseEntity.ok(allFlights);
    }
}
