package com.aeromiles.model.onetwothree.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RootData {

    @JsonProperty("data")
    private SearchDTO data;

}
