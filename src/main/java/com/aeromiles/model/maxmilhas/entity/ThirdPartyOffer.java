package com.aeromiles.model.maxmilhas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ThirdPartyOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private double amount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "offer_id", nullable = false) // Define que o campo não pode ser nulo
    private Offer offer;

}
