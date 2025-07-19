package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.SegmentsIdentifierDTO;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Getter @Setter
public class SegmentIdentifier {
    private String operatedBy;
    private String carrierCode;
    private String flightNumber;
    private String departureStation;
    private String arrivalStation;
    private LocalDateTime std;
    private LocalDateTime sta;

    public static SegmentIdentifier fromDTO(SegmentsIdentifierDTO dto) {
        if (dto == null) return null;

        SegmentIdentifier entity = new SegmentIdentifier();
        entity.setOperatedBy(dto.getOperatedBy());
        entity.setCarrierCode(dto.getCarrierCode());
        entity.setFlightNumber(dto.getFlightNumber());
        entity.setDepartureStation(dto.getDepartureStation());
        entity.setArrivalStation(dto.getArrivalStation());
        entity.setStd(dto.getStd());
        entity.setSta(dto.getSta());
        //entity.setStops(convertToJson(dto.getStops()));
        return entity;
    }
}
