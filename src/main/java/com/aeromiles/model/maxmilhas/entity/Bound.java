package com.aeromiles.model.maxmilhas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Bound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @OneToMany(mappedBy = "bound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Segment> segments = new ArrayList<>();
}
