package com.aeromiles.model.onetwothree.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RootData {

    @JsonProperty("data")
    private SearchDTO data;

}
