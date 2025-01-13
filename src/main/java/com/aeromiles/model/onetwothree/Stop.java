package com.aeromiles.model.onetwothree;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stop")
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String op;

    private Boolean escale;

    private String flightNumber;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private String departureLocation;

    private String arrivalLocation;

    private String flightDuration;

    private String timeWaiting;

    private String companyCode;

    private String operantCompany;

    private String cabin;

    private Integer qntEscale;

    @ManyToOne
    @JoinColumn(name = "flight_one_two_three_id")
    private FlightOneTwoThree flightOneTwoThree;

}
