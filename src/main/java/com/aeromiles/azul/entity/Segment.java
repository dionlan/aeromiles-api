package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.SegmentsDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "segments")
@Getter @Setter
public class Segment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String segmentKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journey_id")
    private Journey journey;

    @Embedded
    private Equipment equipment;

    @Embedded
    private SegmentIdentifier identifier;

    @OneToMany(mappedBy = "segment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Leg> legs = new ArrayList<>();

    public static Segment fromDTO(SegmentsDTO dto) {
        if (dto == null) return null;

        Segment entity = new Segment();
        entity.setSegmentKey(dto.getSegmentKey());
        entity.setEquipment(Equipment.fromDTO(dto.getEquipment()));
        entity.setIdentifier(SegmentIdentifier.fromDTO(dto.getIdentifier()));

        if (dto.getLegs() != null) {
            entity.setLegs(dto.getLegs().stream()
                .map(Leg::fromDTO)
                .peek(l -> l.setSegment(entity))
                .collect(Collectors.toList()));
        }

        return entity;
    }
}
