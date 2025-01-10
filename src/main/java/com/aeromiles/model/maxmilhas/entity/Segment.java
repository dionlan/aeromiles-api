package com.aeromiles.model.maxmilhas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Segment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String operatingFlightNumber;

    @Column(nullable = false)
    private String duration;

    @Column(nullable = false)
    private Integer stopQuantity;

    @Column(nullable = false)
    private String idSegment;

    @Column(nullable = false)
    private String cabin;

    @Column(nullable = false)
    private String bookingClass;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bound_id")
    private Bound bound;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "departure_id", referencedColumnName = "id")
    private Location departure;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "arrival_id", referencedColumnName = "id")
    private Location arrival;

}
