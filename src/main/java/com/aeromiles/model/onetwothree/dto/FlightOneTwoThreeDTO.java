package com.aeromiles.model.onetwothree.dto;

import com.aeromiles.model.maxmilhas.dto.AirportDTO;
import com.aeromiles.model.onetwothree.FlightOneTwoThree;
import com.aeromiles.model.onetwothree.Search;
import com.aeromiles.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.aeromiles.service.AirportService.getAirport;
import static java.util.Objects.isNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightOneTwoThreeDTO {

    @JsonProperty("KEY")
    private String key;

    @JsonProperty("UNIQUE_ID")
    private String uniqueId;

    @JsonProperty("UNIQUE_ID_CLEAN")
    private String uniqueIdClean;

    @JsonProperty("AIRLINE")
    private AirlineOneTwoThreeDTO airline;

    @JsonProperty("LUGGAGE_QUANTITY")
    private Integer luggageQuantity;

    @JsonProperty("FLIGHT_NUMBER")
    private String flightNumber;

    @JsonProperty("CLASS_SERVICE")
    private Integer classService;

    @JsonProperty("DEPARTURE_TIME")
    private String departureTime;

    @JsonProperty("DEPARTURE_LOCATION")
    private String departureLocation;

    @JsonProperty("ARRIVAL_LOCATION")
    private String arrivalLocation;

    @JsonProperty("ARRIVAL_TIME")
    private String arrivalTime;

    @JsonProperty("ARRIVAL_REGION")
    private String arrivalRegion;

    @JsonProperty("DEPARTURE_REGION")
    private String departureRegion;

    @JsonProperty("PRICE_MILES_VIP")
    private Integer priceMilesVip;

    @JsonProperty("OUR_PRICE")
    private Double ourPrice;

    @JsonProperty("OUR_PRICE_INFANT")
    private Double ourPriceInfant;

    @JsonProperty("AIRLINE_PRICE")
    private Double airlinePrice;

    @JsonProperty("BEST_AIRLINE_PRICE")
    private Double bestAirlinePrice;

    @JsonProperty("AWARD_PRICE")
    private int awardPrice;

    @JsonProperty("FARE_123MILHAS")
    private Double fare123Milhas;

    @JsonProperty("AIRLINE_PRICE_INFANT")
    private Double airlinePriceInfant;

    @JsonProperty("TAX")
    private Double tax;

    @JsonProperty("TOTAL_TAX")
    private Double totalTax;

    @JsonProperty("ECONOMY")
    private Double economy;

    @JsonProperty("TOTAL_PRICE")
    private Double totalPrice;

    @JsonProperty("QNT_ESCALE")
    private Integer qntEscale;

    @JsonProperty("QNT_CONNECTION")
    private Integer qntConnection;

    @JsonProperty("QNT_STOP")
    private Integer qntStop;

    @JsonProperty("ESCALE")
    private String escale;

    @JsonProperty("DAYS_BETWEEN_DEPARTURE_AND_ARRIVAL")
    private Integer daysBetweenDepartureAndArrival;

    @JsonProperty("TOTAL_FLIGHT_DURATION")
    private String totalFlightDuration;

    @JsonProperty("STOPS")
    private List<StopDTO> stops;

    @JsonProperty("TARIFFED_BABY")
    private Boolean tariffedBaby;

    @JsonProperty("PARSER_NAME")
    private String parserName;

    @JsonProperty("HAND_LUGGAGE")
    private Boolean handLuggage;

    @JsonProperty("IS_FARE_BASE_WITH_DISCOUNT")
    private Boolean isFareBaseWithDiscount;

    @JsonProperty("WARNING_ESCALE")
    private Boolean warningEscale;

    @JsonProperty("SEMI_EXECUTIVE")
    private Boolean semiExecutive;

    @JsonProperty("MILES")
    private Integer miles;

    @JsonProperty("TOKEN_FINGERPRINT_CLEARSALE")
    private String tokenFingerprintClearsale;

    @JsonProperty("departure_airport")
    private AirportDTO departureAirport;

    @JsonProperty("arrival_airport")
    private AirportDTO arrivalAirport;

    public static FlightOneTwoThree toEntity(FlightOneTwoThreeDTO dto) {
        FlightOneTwoThree flight = new FlightOneTwoThree();
        flight.setKey(dto.getKey());
        flight.setUniqueId(dto.getUniqueId());
        flight.setUniqueIdClean(dto.getUniqueIdClean());
        flight.setLuggageQuantity(dto.getLuggageQuantity());
        flight.setFlightNumber(dto.getFlightNumber());
        flight.setClassService(dto.getClassService());
        flight.setDepartureTime(DateUtil.stringToLocalDateTimeUTC(dto.getDepartureTime()));
        flight.setDepartureLocation(dto.getDepartureLocation());
        flight.setArrivalLocation(dto.getArrivalLocation());
        flight.setArrivalTime(DateUtil.stringToLocalDateTimeUTC(dto.getArrivalTime()));
        flight.setArrivalRegion(dto.getArrivalRegion());
        flight.setDepartureRegion(dto.getDepartureRegion());
        flight.setPriceMilesVip(dto.getPriceMilesVip());
        flight.setOurPrice(dto.getOurPrice());
        flight.setOurPriceInfant(dto.getOurPriceInfant());
        flight.setAirlinePrice(dto.getAirlinePrice());
        flight.setBestAirlinePrice(dto.getBestAirlinePrice());
        flight.setAwardPrice(dto.getAwardPrice());
        flight.setFare123Milhas(dto.getFare123Milhas());
        flight.setAirlinePriceInfant(dto.getAirlinePriceInfant());
        flight.setTax(dto.getTax());
        flight.setTotalTax(dto.getTotalTax());
        flight.setEconomy(dto.getEconomy());
        flight.setTotalPrice(dto.getTotalPrice());
        flight.setQntEscale(dto.getQntEscale());
        flight.setQntConnection(dto.getQntConnection());
        flight.setQntStop(dto.getQntStop());
        flight.setEscale(dto.getEscale());
        flight.setDaysBetweenDepartureAndArrival(dto.getDaysBetweenDepartureAndArrival());
        flight.setTotalFlightDuration(setDurationFormatado(dto.getTotalFlightDuration()));
        flight.setTariffedBaby(dto.getTariffedBaby());
        flight.setParserName(dto.getParserName());
        flight.setHandLuggage(dto.getHandLuggage());
        flight.setIsFareBaseWithDiscount(dto.getIsFareBaseWithDiscount());
        flight.setWarningEscale(dto.getWarningEscale());
        flight.setSemiExecutive(dto.getSemiExecutive());
        flight.setMiles(isNull(dto.getMiles()) ? dto.getAwardPrice() : dto.getMiles());
        flight.setTokenFingerprintClearsale(dto.getTokenFingerprintClearsale());
        flight.setAirlineOneTwoThree(AirlineOneTwoThreeDTO.toEntity(dto.getAirline()));
        flight.setStops(dto.getStops().stream().map(StopDTO::toEntity).collect(Collectors.toList()));
        if(isNull(getAirport(dto.getArrivalLocation()))){
            System.out.println(dto.getArrivalLocation());
        }else{
            flight.setArrivalAirport(getAirport(dto.getArrivalLocation()));
        }
        flight.setDepartureAirport(getAirport(dto.getDepartureLocation()));
        return flight;
    }

    public static String setDurationFormatado(String duration) {
        // Verifica se a string já está no formato "Xh YYmin"
        if (duration.matches("\\d+h \\d{2}min")) {
            return duration; // Retorna a string original sem modificações
        }

        // Caso contrário, faz a conversão
        String[] parts = duration.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return String.format("%dh %02dmin", hours, minutes);
    }
}
