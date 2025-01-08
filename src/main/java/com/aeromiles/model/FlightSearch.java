package com.aeromiles.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class FlightSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String searchId;
    private String tripType;
    private String fromCity;
    private String toCity;
    private LocalDate outboundDate;
    private LocalDate inboundDate;
    private int adults;
    private int children;
    private int infants;
    private String cabin;
}
