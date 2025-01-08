package com.aeromiles.service;

import com.aeromiles.model.Flight;
import com.aeromiles.repository.FlightRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Service
public class FlightService {

    private static final String API_URL_TEMPLATE = "https://api-air-flightsearch-green.smiles.com.br/v1/airlines/search?cabin=ALL&originAirportCode=%s&destinationAirportCode=%s&departureDate=%s&adults=%d";

    private static final String API_URL_TEMPLATE_1 = "https://api-air-flightsearch-prd.smiles.com.br/v1/airlines/search?cabin=ALL&originAirportCode=%s&destinationAirportCode=%s&departureDate=%s&adults=%d";

    private static final String API_URL_TEMPLATE_2 = "https://api-airlines-boarding-tax-prd.smiles.com.br/v1/airlines/search?cabin=ALL&originAirportCode=%s&destinationAirportCode=%s&departureDate=%s&adults=%d";

    //https://github.com/evictorero/smiles/blob/main/main.go
    private static final OkHttpClient client = new OkHttpClient();

    @Autowired
    private FlightRepository flightRepository;

    public List<Flight> searchFlights(String departureAirport, String arrivalAirport, String departureTime, Integer passengers) {
        String url = String.format(API_URL_TEMPLATE, departureAirport, arrivalAirport, departureTime, passengers);

        Request request = new Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0")
            .addHeader("X-Api-Key", "aJqPU7xNHl9qN3NVZnPaJ208aPo2Bh2p2ZV844tw")
            .addHeader("Origin", "https://www.smiles.com.br")
            .addHeader("Region", "BRASIL")
            .addHeader("Referer", "https://www.smiles.com.br/")
            .addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
            .addHeader("Language", "pt-BR")
            .addHeader("Channel", "Web")
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody;
                if ("gzip".equalsIgnoreCase(response.header("Content-Encoding"))) {
                    try (GZIPInputStream gzip = new GZIPInputStream(response.body().byteStream());
                         BufferedReader br = new BufferedReader(new InputStreamReader(gzip))) {
                        responseBody = br.lines().collect(Collectors.joining());
                    }
                } else {
                    responseBody = response.body().string();
                }

                List<Flight> flights = ParseFlightJsonService.parseJsonResponse(responseBody);
                flightRepository.saveAll(flights);
                return flights;
            }
        } catch (Exception e) {
            System.err.println("Error fetching flights for " + departureTime);
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
}
