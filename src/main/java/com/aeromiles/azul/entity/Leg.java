package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.LegDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "legs")
@Getter @Setter
public class Leg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String legKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segment_id")
    private Segment segment;

    @Embedded
    private LegIdentifier identifier;

    public static Leg fromDTO(LegDTO dto) {
        if (dto == null) return null;

        Leg entity = new Leg();
        entity.setLegKey(dto.getLegKey());
        entity.setIdentifier(LegIdentifier.fromDTO(dto.getIdentifier()));
        return entity;
    }
}
