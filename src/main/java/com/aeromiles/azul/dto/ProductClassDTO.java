package com.aeromiles.azul.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductClassDTO {
    private String code;
    private String category;
    private String name;
}
