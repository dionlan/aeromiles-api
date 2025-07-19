package com.aeromiles.azul.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LegDTO {
    private String legKey;
    private LegsIdentifierDTO identifier;
}
