package com.aeromiles.azul.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class FareInformationDTO {
    private BigDecimal lowestPoints;
    private BigDecimal highestPoints;
}
