package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.LegsIdentifierDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Getter @Setter
public class LegIdentifier {
    private String carrierCode;
    private String flightNumber;
    private String departureStation;
    private String arrivalStation;
    private LocalDateTime std;
    private LocalDateTime sta;

    @Embedded
    private DurationEmbeddable duration;

    public static LegIdentifier fromDTO(LegsIdentifierDTO dto) {
        if (dto == null) return null;

        LegIdentifier entity = new LegIdentifier();
        entity.setCarrierCode(dto.getCarrierCode());
        entity.setFlightNumber(dto.getFlightNumber());
        entity.setDepartureStation(dto.getDepartureStation());
        entity.setArrivalStation(dto.getArrivalStation());
        entity.setStd(dto.getStd());
        entity.setSta(dto.getSta());
        entity.setDuration(DurationEmbeddable.fromDTO(dto.getDuration()));
        return entity;
    }
}
