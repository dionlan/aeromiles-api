package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.PointsDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter @Setter
public class Points {
    private BigDecimal amount;
    private BigDecimal discountedAmount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "discount_amount")),
        @AttributeOverride(name = "promotionCode", column = @Column(name = "discount_promo_code")),
        @AttributeOverride(name = "discountRuleForContactPax", column = @Column(name = "discount_rule_contact_pax")),
        @AttributeOverride(name = "applied", column = @Column(name = "discount_applied"))
    })
    private Discount discount;

    public static Points fromDTO(PointsDTO dto) {
        if (dto == null) return null;

        Points entity = new Points();
        entity.setAmount(dto.getAmount());
        entity.setDiscount(Discount.fromDTO(dto.getDiscount()));
        entity.setDiscountedAmount(dto.getDiscountedAmount());
        return entity;
    }
}
