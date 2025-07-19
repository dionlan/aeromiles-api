package com.aeromiles.azul.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaxPointsDTO {
    private int amountLevel;
    private BigDecimal discountedAmount;
    private List<LevelsDTO> levels;
}
