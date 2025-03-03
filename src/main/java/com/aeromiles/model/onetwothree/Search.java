package com.aeromiles.model.onetwothree;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long searchId;

    private Boolean showPromoFlight;

    private Double pricePromoFlight;

    private String linkPromoFlight;

    private Boolean showPromoFlightOneWay;

    private Double pricePromoFlightOneWay;

    private String linkPromoFlightOneWay;

    @OneToMany(mappedBy = "search", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlightOneTwoThree> flights;
}
