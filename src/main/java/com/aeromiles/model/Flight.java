package com.aeromiles.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;
    @Column(name = "departure_airport")
    private String departureAirport;
    @Column(name = "arrival_airport")
    private String arrivalAirport;
    @Column(name = "departure_date")
    private String departureDate;
    @Column(name = "departure_time")
    private String departureTime;
    @Column(name = "arrival_time")
    private String arrivalTime;
    private int stops;
    @Column(name = "source_gds")
    private String sourceGds;
    @Column(name = "flight_number")
    private int flightNumber;
    private int miles;
    @Column(name = "miles_back")
    private int milesBack;
    @Column(name = "available_seats")
    private int availableSeats;
    private String url;

    @Column(name = "airline")
    private String airline;

    @Column(name = "price")
    private double price;
}
