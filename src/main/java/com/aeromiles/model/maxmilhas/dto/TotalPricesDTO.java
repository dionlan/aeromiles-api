package com.aeromiles.model.maxmilhas.dto;

import lombok.Data;

@Data
public class TotalPricesDTO {

    private double base;
    private double total;
    private String currencyCode;
    private double totalTaxes;
    private double totalFees;
    private double totalDiscounts;
}
