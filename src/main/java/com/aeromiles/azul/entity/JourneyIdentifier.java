package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.JourneysIdentifierDTO;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Embeddable
@Getter @Setter
public class JourneyIdentifier {

    private String operatedBy;
    private String carrierCode;
    private String flightNumber;
    private String departureStation;
    private String arrivalStation;

    private LocalDateTime std;
    private LocalDateTime sta;

    @Embedded
    private DurationEmbeddable duration;

    @Embedded
    private Connection connections;

    public static JourneyIdentifier fromDTO(JourneysIdentifierDTO dto) {
        if (dto == null) return null;

        JourneyIdentifier entity = new JourneyIdentifier();
        entity.setOperatedBy(dto.getOperatedBy());
        entity.setCarrierCode(dto.getCarrierCode());
        entity.setFlightNumber(dto.getFlightNumber());
        entity.setDepartureStation(dto.getDepartureStation());
        entity.setArrivalStation(dto.getArrivalStation());
        entity.setStd(dto.getStd());
        entity.setSta(dto.getSta());

        if (dto.getDuration() != null) {
            entity.setDuration(DurationEmbeddable.fromDTO(dto.getDuration()));
        }

        if (dto.getConnections() != null) {
            entity.setConnections(Connection.fromDTO(dto.getConnections()));
        }

        return entity;
    }
}
