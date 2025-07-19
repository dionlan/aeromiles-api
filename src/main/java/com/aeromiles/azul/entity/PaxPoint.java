package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.PaxPointsDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "pax_points")
@Getter @Setter
public class PaxPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int amountLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fare_id")
    private Fare fare;

    @OneToMany(mappedBy = "paxPoint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Level> levels = new ArrayList<>();

    public static PaxPoint fromDTO(PaxPointsDTO dto) {
        if (dto == null) return null;

        PaxPoint entity = new PaxPoint();
        entity.setAmountLevel(dto.getAmountLevel());

        if (dto.getLevels() != null) {
            entity.setLevels(dto.getLevels().stream()
                .map(Level::fromDTO)
                .peek(l -> l.setPaxPoint(entity))  // <-- Isso é essencial
                .collect(Collectors.toList()));
        }

        return entity;
    }
}
