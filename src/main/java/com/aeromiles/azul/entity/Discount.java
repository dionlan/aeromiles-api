package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.DiscountDTO;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter @Setter
public class Discount {
    private boolean applied;
    private BigDecimal amount;
    private String promotionCode;
    private boolean discountRuleForContactPax;

    public static Discount fromDTO(DiscountDTO dto) {
        if (dto == null) return null;

        Discount entity = new Discount();
        entity.setApplied(dto.isApplied());
        entity.setAmount(dto.getAmount());
        entity.setPromotionCode(dto.getPromotionCode());
        return entity;
    }
}
