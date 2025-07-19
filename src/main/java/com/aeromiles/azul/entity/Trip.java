package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.JourneyDTO;
import com.aeromiles.azul.dto.TripDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "trips")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String departureStation;
    private String arrivalStation;

    private LocalDateTime std;

    private String currencyCode;
    private String flightType;
    private String region;

    @Embedded
    private FareInformation fareInformation;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Journey> journeys = new ArrayList<>();

    public static Trip fromDTO(TripDTO dto) {
        if (dto == null) return null;
        Trip entity = new Trip();
        entity.setDepartureStation(dto.getDepartureStation());
        entity.setArrivalStation(dto.getArrivalStation());
        entity.setStd(dto.getStd());
        entity.setCurrencyCode(dto.getCurrencyCode());
        entity.setFlightType(dto.getFlightType());
        entity.setRegion(dto.getRegion());

        if (dto.getFareInformation() != null) {
            FareInformation fareInfo = FareInformation.fromDTO(dto.getFareInformation());
            entity.setFareInformation(fareInfo);
        }

        if (dto.getJourneys() != null) {
            List<Journey> journeys = dto.getJourneys().stream()
                .map(Journey::fromDTO)
                .peek(journey -> journey.setTrip(entity))
                .collect(Collectors.toList());
            entity.setJourneys(journeys);
        }

        return entity;
    }
}
