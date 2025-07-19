package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.StationsInformationDTO;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter @Setter
public class StationInformation {
    private String code;
    private String duration;

    public static StationInformation fromDTO(StationsInformationDTO dto) {
        if (dto == null) return null;

        StationInformation entity = new StationInformation();
        entity.setCode(dto.getCode());
        entity.setDuration(dto.getDuration());
        return entity;
    }
}
