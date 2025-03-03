package com.aeromiles.model.onetwothree.dto;

import com.aeromiles.model.onetwothree.FlightOneTwoThree;
import com.aeromiles.model.onetwothree.Search;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
            List<FlightOneTwoThree> flights = dto.getFlights().entrySet().stream()
                .map(entry -> {
                    FlightOneTwoThree flight = FlightOneTwoThreeDTO.toEntity(entry.getValue());
                    flight.setUniqueId(entry.getKey());
                    flight.setSearch(search);
                    return flight;
                }).collect(Collectors.toList());
            search.setFlights(flights);
        }

        return search;
    }

    public List<FlightOneTwoThree> toEntityList() {
        if (flights == null || flights.isEmpty()) {
            return Collections.emptyList();
        }
        return flights.values().stream()
            .map(FlightOneTwoThreeDTO::toEntity) // Converte cada DTO para a entidade correspondente
            .toList();
    }
}
