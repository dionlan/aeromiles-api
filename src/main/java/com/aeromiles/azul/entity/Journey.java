package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.JourneyDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journeys")
@Getter @Setter
public class Journey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String journeyKey;
    private String journeySellKey;

    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Embedded
    private JourneyIdentifier identifier;

    @OneToMany(mappedBy = "journey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fare> fares = new ArrayList<>();

    @OneToMany(mappedBy = "journey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Segment> segments = new ArrayList<>();

    public static Journey fromDTO(JourneyDTO dto) {
        Journey entity = new Journey();
        entity.setJourneyKey(dto.getJourneyKey());
        entity.setJourneySellKey(dto.getJourneySellKey());

        if (dto.getIdentifier() != null) {
            entity.setIdentifier(JourneyIdentifier.fromDTO(dto.getIdentifier()));
        }

        if (dto.getFares() != null) {
            List<Fare> fares = dto.getFares().stream()
                .map(Fare::fromDTO)
                .peek(fare -> fare.setJourney(entity))
                .toList();
            entity.setFares(fares);
        }

        if (dto.getSegments() != null) {
            List<Segment> segments = dto.getSegments().stream()
                .map(Segment::fromDTO)
                .peek(segment -> segment.setJourney(entity))
                .toList();
            entity.setSegments(segments);
        }

        return entity;
    }
}
