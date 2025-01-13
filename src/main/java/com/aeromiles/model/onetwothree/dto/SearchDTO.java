package com.aeromiles.model.onetwothree.dto;

import com.aeromiles.model.onetwothree.FlightOneTwoThree;
import com.aeromiles.model.onetwothree.Search;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

@Data
public class SearchDTO {

    @JsonProperty("SEARCH_ID")
    private Long searchId;

    @JsonProperty("SHOW_PROMO_FLIGHT")
    private Boolean showPromoFlight;

    @JsonProperty("PRICE_PROMO_FLIGHT")
    private Double pricePromoFlight;

    @JsonProperty("LINK_PROMO_FLIGHT")
    private String linkPromoFlight;

    @JsonProperty("SHOW_PROMO_FLIGHT_ONE_WAY")
    private Boolean showPromoFlightOneWay;

    @JsonProperty("PRICE_PROMO_FLIGHT_ONE_WAY")
    private Double pricePromoFlightOneWay;

    @JsonProperty("LINK_PROMO_FLIGHT_ONE_WAY")
    private String linkPromoFlightOneWay;

    @JsonProperty("FLIGHTS")
    private Map<String, FlightOneTwoThreeDTO> flights;

    public static Search toEntity(SearchDTO dto) {
        Search search = new Search();
        search.setSearchId(dto.getSearchId());
        search.setShowPromoFlight(dto.getShowPromoFlight());
        search.setPricePromoFlight(dto.getPricePromoFlight());
        search.setLinkPromoFlight(dto.getLinkPromoFlight());
        search.setShowPromoFlightOneWay(dto.getShowPromoFlightOneWay());
        search.setPricePromoFlightOneWay(dto.getPricePromoFlightOneWay());
        search.setLinkPromoFlightOneWay(dto.getLinkPromoFlightOneWay());

        if (dto.getFlights() != null) {
            Map<String, FlightOneTwoThree> flightEntities = dto.getFlights().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                    entry -> {
                        FlightOneTwoThree flight = FlightOneTwoThreeDTO.toEntity(entry.getValue());
                        flight.setUniqueId(entry.getKey());
                        return flight;
                    }));
            search.setFlights(flightEntities);
        }

        return search;
    }
}
