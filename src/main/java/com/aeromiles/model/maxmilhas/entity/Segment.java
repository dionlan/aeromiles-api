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

}
