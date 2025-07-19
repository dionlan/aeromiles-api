package com.aeromiles.azul.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SegmentsDTO {
    private String segmentKey;
    private EquipmentDTO equipment;
    private SegmentsIdentifierDTO identifier;
    private List<LegDTO> legs;
}
