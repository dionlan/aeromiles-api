package com.aeromiles.model.onetwothree.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightOneTwoThreeResponseDTO {

    private List<FlightDTO> flights;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlightDTO {
        private String _id;
        private String airlineLogo;
        private String airlineName;
        private String passengerType;
        private String stopType;
        private String refundableStatus;
        private FlightInfoDTO flightInfo;
        private List<CancellationRuleDTO> cancellationRules;
        private List<DateChangeRuleDTO> dateChangeRules;
        private List<String> notes;
        private String duration;
        private AvailableSeatsDTO availableSeats;
        private DepartureDTO departure;
        private ArrivalDTO arrival;
        private FareSummaryDTO fareSummary;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlightInfoDTO {
        private String flightNumber;
        private String aircraft;
        private String operatedBy;

        @JsonProperty("class")
        private String classType;
        private String baggage;
        private String checkIn;
        private String cabin;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancellationRuleDTO {
        private String rule;
        private int amountPerKm;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateChangeRuleDTO {
        private String rule;
        private int amountPerKm;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableSeatsDTO {
        private String flightId;
        private String totalSeat;
        private String available;
        private List<SeatDTO> seats;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatDTO {
        private String seatNo;
        private boolean available;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartureDTO {
        private String code;
        private String time;
        private String date;
        private String city;
        private String terminal;
        private String airportName;
        private String seats;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArrivalDTO {
        private String code;
        private String time;
        private String date;
        private String city;
        private String terminal;
        private String airportName;
        private String seats;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FareSummaryDTO {
        private Double baseFare;
        private Double taxesAndFees;
        private Double total;
        private Integer miles;
    }
}
