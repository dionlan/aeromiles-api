package com.aeromiles.azul.dto;

import com.aeromiles.azul.entity.ResponseData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AzulApiResponse {
    private ResponseData data;
}
