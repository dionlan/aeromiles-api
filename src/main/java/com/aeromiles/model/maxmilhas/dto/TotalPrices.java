package com.aeromiles.model.maxmilhas.dto;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class TotalPrices {

    private double base;

    private double total;

    private String currencyCode;

    private double totalTaxes;

    private double totalFees;

    private double totalDiscounts;
}
