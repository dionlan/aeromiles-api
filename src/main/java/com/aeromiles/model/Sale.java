package com.aeromiles.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "destination")
    private String destination;

    @Column(name = "price")
    private double price;

    @Column(name = "image")
    private String image;
}
