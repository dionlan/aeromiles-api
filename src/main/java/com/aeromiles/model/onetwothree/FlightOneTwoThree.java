package com.aeromiles.model.onetwothree;

import com.aeromiles.model.Airport;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class FlightOneTwoThree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uniqueId;

    private String key;

    private String uniqueIdClean;

    private Integer luggageQuantity;

    private String flightNumber;

    private Integer classService;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private String arrivalRegion;

    private String departureRegion;

    private Integer priceMilesVip;

    private Double ourPrice;

    private Double ourPriceInfant;

    private Double airlinePrice;

    private Double bestAirlinePrice;

    private int awardPrice;

    private Double fare123Milhas;

    private Double airlinePriceInfant;

    private Double tax;

    private Double totalTax;

    private Double economy;

    private Double totalPrice;

    private Integer qntEscale;

    private Integer qntConnection;

    private Integer qntStop;

    private String escale;

    private Integer daysBetweenDepartureAndArrival;

    private String totalFlightDuration;

    private Boolean tariffedBaby;

    private String parserName;

    private Boolean handLuggage;

    private Boolean isFareBaseWithDiscount;

    private Boolean warningEscale;

    private Boolean semiExecutive;

    private Integer miles;

    private String tokenFingerprintClearsale;

    @ManyToOne
    @JoinColumn(name = "search_id", nullable = false)
    private Search search;

    @OneToMany(mappedBy = "flightOneTwoThree", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stop> stops;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "airline_one_two_three_id", referencedColumnName = "id")
    private AirlineOneTwoThree airlineOneTwoThree;

    @Column(name = "departure_location", nullable = false) // Adiciona a coluna explicitamente
    private String departureLocation;

    @Column(name = "arrival_location", nullable = false) // Adiciona a coluna explicitamente
    private String arrivalLocation;

    @ManyToOne
    @JoinColumn(name = "departure_airport", referencedColumnName = "code")
    private Airport departureAirport;

    @ManyToOne
    @JoinColumn(name = "arrival_airport", referencedColumnName = "code")
    private Airport arrivalAirport;

}
