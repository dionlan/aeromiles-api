package com.aeromiles.service;

import com.aeromiles.model.Flight;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParseFlightJsonService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATTER_IDA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseFlightJsonService.class);

    public static List<Flight> parseJsonResponse(String responseBody) {
        JsonObject json = new Gson().fromJson(responseBody, JsonObject.class);
        JsonArray flightSegmentList = json.getAsJsonArray("requestedFlightSegmentList");

        List<Flight> flights = new ArrayList<>();
        JsonObject passengers = json.getAsJsonObject("passenger");
        int passengerCount = passengers.get("adults").getAsInt();

        flightSegmentList.forEach(segmentElement -> {
            JsonObject segment = segmentElement.getAsJsonObject();
            JsonArray flightList = segment.getAsJsonArray("flightList");

            flightList.forEach(flightElement -> {
                JsonObject flight = flightElement.getAsJsonObject();

                JsonArray legList = flight.getAsJsonArray("legList");
                JsonObject departure = flight.getAsJsonObject("departure");
                JsonObject arrival = flight.getAsJsonObject("arrival");
                JsonArray fareList = flight.getAsJsonArray("fareList");

                int milesBack = flight.get("milesBack").getAsInt();
                int availableSeats = flight.get("availableSeats").getAsInt();

                int stops = flight.get("stops").getAsInt();
                String sourceGds = flight.get("sourceGDS").getAsString();
                int flightNumber = legList.get(0).getAsJsonObject().get("flightNumber").getAsInt();
                String departureAirport = departure.getAsJsonObject("airport").get("code").getAsString();
                String arrivalAirport = arrival.getAsJsonObject("airport").get("code").getAsString();
                String departureDate = LocalDateTime.parse(departure.get("date").getAsString()).format(FORMATTER_DATE);
                String ida = LocalDateTime.parse(departure.get("date").getAsString()).format(FORMATTER_IDA);;
                String departureTime = LocalDateTime.parse(departure.get("date").getAsString()).format(FORMATTER);
                String arrivalTime = LocalDateTime.parse(arrival.get("date").getAsString()).format(FORMATTER);

                if (fareList != null && fareList.size() > 0) {
                    // Processa a tarifa
                    JsonObject fare = fareList.size() > 1 ? fareList.get(1).getAsJsonObject() : fareList.get(0).getAsJsonObject();
                    int miles = fare.get("miles").getAsInt();
                    Double costTax = fare.has("g3") ? fare.getAsJsonObject("g3").get("costTax").getAsDouble() : null;
                    String url = "https://b2c.voegol.com.br/compra/busca-parceiros?"
                            + "pv=BR&tipo=DF&de=" + departureAirport
                            + "&para="+arrivalAirport+"&ida=" + ida
                            + "&ADT=1&CHD=0&INF=0&vooSelecionado1="
                            + sourceGds + flightNumber + "|201219BRL";

                    Flight flightModel = buildFlightModel(departureAirport, arrivalAirport, departureDate, departureTime, arrivalTime, stops,
                            sourceGds, flightNumber, miles, milesBack, availableSeats, url);
                    flights.add(flightModel);
                } else {
                    LOGGER.warn("fareList está vazia para o voo com origem {} e destino {}", departureAirport, arrivalAirport);
                }
            });
        });

        return flights;
    }

    private static Flight buildFlightModel(String departureAirport, String arrivalAirport, String departureDate, String departureTime, String arrivalTime, int stops,
                                           String sourceGds, Integer flightNumber, int miles, int milesBack, int availableSeats, String url) {
        Flight flight = new Flight();
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureDate(departureDate);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setStops(stops);
        flight.setSourceGds(sourceGds);
        flight.setFlightNumber(flightNumber);
        flight.setMiles(miles);
        flight.setMilesBack(milesBack);
        flight.setAvailableSeats(availableSeats);
        flight.setUrl(url);
        return flight;
    }

    private static String formataParadas(String stops){
        if(Integer.parseInt(stops) > 0){
            return stops + "Parada(s)";
        }
        return "Direto";
    }
}
