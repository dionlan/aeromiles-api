package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.EquipmentDTO;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter @Setter
public class Equipment {
    private String name;
    private String suffix;
    private String type;

    public static Equipment fromDTO(EquipmentDTO dto) {
        if (dto == null) return null;

        Equipment entity = new Equipment();
        entity.setName(dto.getName());
        entity.setSuffix(dto.getSuffix());
        entity.setType(dto.getType());
        return entity;
    }
}
