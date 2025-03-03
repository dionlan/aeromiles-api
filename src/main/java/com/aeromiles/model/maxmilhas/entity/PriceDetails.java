package com.aeromiles.model.maxmilhas.entity;

import com.aeromiles.model.maxmilhas.dto.TotalPrices;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PriceDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private TotalPrices totalPrices;
}
