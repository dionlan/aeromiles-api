package com.aeromiles.model.maxmilhas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OfferDTO {

    @JsonProperty("id")
    private String idOffer;

    @JsonProperty("comparativeLevel")
    private String comparativeLevel;

    @JsonProperty("type")
    private String type;

    @JsonProperty("thirdPartyOffers")
    private List<ThirdPartyOfferDTO> thirdPartyOffers;

    @JsonProperty("bounds")
    private List<BoundDTO> bounds;
}
