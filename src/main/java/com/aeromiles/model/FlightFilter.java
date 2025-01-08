package com.aeromiles.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class FlightFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "airline")
    private String airline;

    @Column(name = "price")
    private double price;
}
