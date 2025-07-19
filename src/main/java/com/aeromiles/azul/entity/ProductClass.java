package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.ProductClassDTO;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter @Setter
public class ProductClass {
    private String code;
    private String category;
    private String name;

    public static ProductClass fromDTO(ProductClassDTO dto) {
        if (dto == null) return null;

        ProductClass entity = new ProductClass();
        entity.setCode(dto.getCode());
        entity.setCategory(dto.getCategory());
        entity.setName(dto.getName());
        return entity;
    }
}
