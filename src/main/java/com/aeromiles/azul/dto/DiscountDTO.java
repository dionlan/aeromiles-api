package com.aeromiles.azul.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscountDTO {
    private boolean applied;
    private BigDecimal amount;
    private String promotionCode;
    private boolean discountRuleForContactPax;
}
