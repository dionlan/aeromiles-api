package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.DurationDTO;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
public class Duration {
    private int days;
    private int hours;
    private int minutes;

    public static Duration fromDTO(DurationDTO dto) {
        if (dto == null) return null;

        Duration entity = new Duration();
        entity.setDays(dto.getDays());
        entity.setHours(dto.getHours());
        entity.setMinutes(dto.getMinutes());
        return entity;
    }
}
