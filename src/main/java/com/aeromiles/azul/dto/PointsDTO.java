package com.aeromiles.azul.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PointsDTO {
    private BigDecimal amount;
    private DiscountDTO discount;
    private BigDecimal discountedAmount;
}
