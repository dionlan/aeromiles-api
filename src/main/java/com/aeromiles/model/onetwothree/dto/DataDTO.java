package com.aeromiles.model.onetwothree.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class DataDTO {

    @JsonProperty("SEARCH_ID")
    private Long searchId;

    @JsonProperty("SHOW_PROMO_FLIGHT")
    private boolean showPromoFlight;

    @JsonProperty("PRICE_PROMO_FLIGHT")
    private Double pricePromoFlight;

    @JsonProperty("LINK_PROMO_FLIGHT")
    private String linkPromoFlight;

    @JsonProperty("SHOW_PROMO_FLIGHT_ONE_WAY")
    private boolean showPromoFlightOneWay;

    @JsonProperty("PRICE_PROMO_FLIGHT_ONE_WAY")
    private Double pricePromoFlightOneWay;

    @JsonProperty("LINK_PROMO_FLIGHT_ONE_WAY")
    private String linkPromoFlightOneWay;

    @JsonProperty("FLIGHTS")
    private Map<String, FlightOneTwoThreeDTO> flights;
}
