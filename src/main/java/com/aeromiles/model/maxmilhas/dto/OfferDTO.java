package com.aeromiles.model.maxmilhas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OfferDTO {

    @JsonProperty("id")
    private String idOffer;

    private String comparativeLevel;

    private String type;

    private List<ThirdPartyOfferDTO> thirdPartyOffers;

    private List<BoundDTO> bounds;

    private PriceDetailsDTO priceDetails;
}
