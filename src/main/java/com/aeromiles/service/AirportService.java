package com.aeromiles.service;

import com.aeromiles.model.Airport;
import com.aeromiles.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportService {

    @Autowired
    private AirportRepository airportRepository;

    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public List<Airport> searchAirports(String query) {
        return airportRepository.searchByMultipleFields(query);
    }
}
