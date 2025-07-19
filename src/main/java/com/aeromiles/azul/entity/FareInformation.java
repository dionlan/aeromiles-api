package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.FareInformationDTO;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter @Setter
public class FareInformation {
    private BigDecimal lowestPoints;
    private BigDecimal highestPoints;

    public static FareInformation fromDTO(FareInformationDTO dto) {
        FareInformation entity = new FareInformation();
        entity.setLowestPoints(dto.getLowestPoints());
        entity.setHighestPoints(dto.getHighestPoints());
        return entity;
    }
}
