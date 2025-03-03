package com.aeromiles.model.maxmilhas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Bound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private FareProfile fareProfile;

    @Column
    private String carrier;

    @Column
    private String validatedBy;

    @Column
    private String duration;

    @Column
    private int daysDifference;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "locationCode", column = @Column(name = "departure_location_code")),
        @AttributeOverride(name = "dateTime", column = @Column(name = "departure_date_time")),
        @AttributeOverride(name = "terminal", column = @Column(name = "departure_terminal"))
    })
    private Location departure;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "locationCode", column = @Column(name = "arrival_location_code")),
        @AttributeOverride(name = "dateTime", column = @Column(name = "arrival_date_time")),
        @AttributeOverride(name = "terminal", column = @Column(name = "arrival_terminal"))
    })
    private Location arrival;

    @Column
    private int totalStops;

    @Column
    private boolean hasCheckedBags;

    @Column
    private boolean hasCarryOnBags;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @OneToMany(mappedBy = "bound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Segment> segments = new ArrayList<>();

}
