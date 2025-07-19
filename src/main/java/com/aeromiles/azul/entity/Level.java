package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.LevelsDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "levels")
@Getter @Setter
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean companionPass;
    private String paxType;
    private BigDecimal fareMoney;
    private BigDecimal taxesAndFees;
    private BigDecimal totalMoney;
    private String currencyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pax_point_id")
    private PaxPoint paxPoint;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "points_amount")),
        @AttributeOverride(name = "discountedAmount", column = @Column(name = "points_discounted_amount"))
    })
    private Points points;

    public static Level fromDTO(LevelsDTO dto) {
        if (dto == null) return null;

        Level entity = new Level();
        entity.setPaxType(dto.getPaxType());
        entity.setFareMoney(dto.getFareMoney());
        entity.setPoints(Points.fromDTO(dto.getPoints()));
        entity.setTaxesAndFees(dto.getTaxesAndFees());
        entity.setTotalMoney(dto.getTotalMoney());
        entity.setCurrencyCode(dto.getCurrencyCode());
        return entity;
    }
}
