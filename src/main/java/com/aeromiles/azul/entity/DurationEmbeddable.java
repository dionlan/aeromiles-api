package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.DurationDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class DurationEmbeddable {
    private int days;
    private int hours;
    private int minutes;

    public static DurationEmbeddable fromDTO(DurationDTO dto) {
        if (dto == null) return null;

        DurationEmbeddable duration = new DurationEmbeddable();
        duration.setDays(dto.getDays());
        duration.setHours(dto.getHours());
        duration.setMinutes(dto.getMinutes());
        return duration;
    }
}
