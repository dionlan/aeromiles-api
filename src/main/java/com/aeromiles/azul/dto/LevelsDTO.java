package com.aeromiles.azul.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LevelsDTO {
    private boolean companionPass;
    private String paxType;
    private BigDecimal fareMoney;
    private PointsDTO points;
    private BigDecimal taxesAndFees;
    private BigDecimal totalMoney;
    private String currencyCode;
}
