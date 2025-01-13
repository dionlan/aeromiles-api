package com.aeromiles.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String iata;

    private String name;

    private String state;

    private String city;

    private String code;

    private String country;

}
