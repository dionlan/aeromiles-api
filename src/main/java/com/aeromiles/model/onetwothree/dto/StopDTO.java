package com.aeromiles.model.onetwothree.dto;

import com.aeromiles.model.onetwothree.Stop;
import com.aeromiles.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StopDTO {

    @JsonProperty("OP")
    private String op;

    @JsonProperty("ESCALE")
    private Boolean escale;

    @JsonProperty("FLIGHT_NUMBER")
    private String flightNumber;

    @JsonProperty("DEPARTURE_TIME")
    private String departureTime;

    @JsonProperty("ARRIVAL_TIME")
    private String arrivalTime;

    @JsonProperty("DEPARTURE_LOCATION")
    private String departureLocation;

    @JsonProperty("ARRIVAL_LOCATION")
    private String arrivalLocation;

    @JsonProperty("FLIGHT_DURATION")
    private String flightDuration;

    @JsonProperty("TIME_WAITING")
    private String timeWaiting;

    @JsonProperty("COMPANY_CODE")
    private String companyCode;

    @JsonProperty("OPERANT_COMPANY")
    private String operantCompany;

    @JsonProperty("CABIN")
    private String cabin;

    @JsonProperty("QNT_ESCALE")
    private Integer qntEscale;

    public static Stop toEntity(StopDTO dto) {
        Stop stop = new Stop();
        stop.setOp(dto.getOp());
        stop.setEscale(dto.getEscale());
        stop.setFlightNumber(dto.getFlightNumber());
        stop.setDepartureTime(DateUtil.stringToLocalDateTimeUTC(dto.getDepartureTime()));
        stop.setArrivalTime(DateUtil.stringToLocalDateTimeUTC(dto.getArrivalTime()));
        stop.setDepartureLocation(dto.getDepartureLocation());
        stop.setArrivalLocation(dto.getArrivalLocation());
        stop.setFlightDuration(dto.getFlightDuration());
        stop.setTimeWaiting(dto.getTimeWaiting());
        stop.setCompanyCode(dto.getCompanyCode());
        stop.setOperantCompany(dto.getOperantCompany());
        stop.setCabin(dto.getCabin());
        stop.setQntEscale(dto.getQntEscale());
        return stop;
    }
}
