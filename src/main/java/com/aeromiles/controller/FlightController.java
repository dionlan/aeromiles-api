package com.aeromiles.controller;

import com.aeromiles.model.Flight;
import com.aeromiles.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping("/search")
    public List<Flight> searchFlights(
            @RequestParam String departureAirport,
            @RequestParam String arrivalAirport,
            @RequestParam String departureTime,
            @RequestParam Integer passengers) {

        return flightService.searchFlights(departureAirport, arrivalAirport, departureTime, passengers);
    }

    @GetMapping("/error")
    public String error() {
        return "An error occurred!";
    }
}   
