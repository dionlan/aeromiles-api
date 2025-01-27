package com.aeromiles.controller;

import com.aeromiles.model.Airport;
import com.aeromiles.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    @Autowired
    private AirportService airportService;

    @GetMapping
    public List<Airport> searchAllAirports() {
        return airportService.getAllAirports();
    }

    @GetMapping("/search")
    public List<Airport> searchAirports(@RequestParam("query") String query) {
        return airportService.searchAirports(query);
    }

    @GetMapping("/error")
    public String error() {
        return "An error occurred!";
    }
}   
