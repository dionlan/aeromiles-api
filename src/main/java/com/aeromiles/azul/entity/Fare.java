package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.FareDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "fares")
@Getter @Setter
public class Fare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String key;
    private boolean available;
    private String classOfService;
    private String fareSellKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journey_id")
    private Journey journey;

    @Embedded
    private ProductClass productClass;

    @OneToMany(mappedBy = "fare", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaxPoint> paxPoints = new ArrayList<>();

    public static Fare fromDTO(FareDTO dto) {
        if (dto == null) return null;

        Fare entity = new Fare();
        entity.setClassOfService(dto.getClassOfService());
        entity.setKey(dto.getKey());
        entity.setAvailable(dto.isAvailable());
        entity.setFareSellKey(dto.getFareSellKey());
        entity.setProductClass(ProductClass.fromDTO(dto.getProductClass()));

        if (dto.getPaxPoints() != null) {
            entity.setPaxPoints(dto.getPaxPoints().stream()
                .map(PaxPoint::fromDTO)
                .peek(p -> p.setFare(entity))
                .collect(Collectors.toList()));
        }

        return entity;
    }
}
