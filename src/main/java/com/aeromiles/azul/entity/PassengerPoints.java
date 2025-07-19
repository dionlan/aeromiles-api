package com.aeromiles.azul.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class PassengerPoints {
    @Id
    @GeneratedValue
    private Long id;

    private String paxType;
    private Double fareMoney;
    private Double pointsAmount;
    private Double discountedPointsAmount;
    private Double taxesAndFees;
    private Double convenienceFee;
    private Double totalMoney;
    private String currencyCode;
}
