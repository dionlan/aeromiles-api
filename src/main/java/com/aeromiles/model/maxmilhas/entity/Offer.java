package com.aeromiles.model.maxmilhas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String idOffer;

    @Column
    private String comparativeLevel;

    @Column(nullable = false)
    private String type;

    @Column
    private String cia;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "price_details_id", referencedColumnName = "id")
    private PriceDetails priceDetails;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThirdPartyOffer> thirdPartyOffers = new ArrayList<>();

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bound> bounds = new ArrayList<>();

    // Método para adicionar ThirdPartyOffers
    public void setThirdPartyOffers(List<ThirdPartyOffer> thirdPartyOffers) {
        this.thirdPartyOffers.clear();
        if (thirdPartyOffers != null) {
            thirdPartyOffers.forEach(thirdPartyOffer -> thirdPartyOffer.setOffer(this));
            this.thirdPartyOffers.addAll(thirdPartyOffers);
        }
    }

    // Método para adicionar Bounds
    public void setBounds(List<Bound> bounds) {
        this.bounds.clear();
        if (bounds != null) {
            bounds.forEach(bound -> bound.setOffer(this));
            this.bounds.addAll(bounds);
        }
    }
}
