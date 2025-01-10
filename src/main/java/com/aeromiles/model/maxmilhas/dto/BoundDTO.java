package com.aeromiles.model.maxmilhas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BoundDTO {

    @JsonProperty("segments")
    private List<SegmentDTO> segments;
}
