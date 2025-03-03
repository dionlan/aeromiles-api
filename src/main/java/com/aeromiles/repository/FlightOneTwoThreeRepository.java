package com.aeromiles.repository;

import com.aeromiles.model.onetwothree.FlightOneTwoThree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FlightOneTwoThreeRepository extends JpaRepository<FlightOneTwoThree, Long> {

    @Query("FROM FlightOneTwoThree f " +
            "INNER JOIN f.arrivalAirport a " +
            "INNER JOIN f.airlineOneTwoThree s " +
            "WHERE f.departureLocation = :departureLocation " +
            "AND f.arrivalAirport.code = :arrivalLocation " +
            "AND DATE(f.departureTime) = :departureDate " +
            "ORDER BY f.totalPrice ASC")
    List<FlightOneTwoThree> findFlightsByLocationsAndDate(
            @Param("departureLocation") String departureLocation,
            @Param("arrivalLocation") String arrivalLocation,
            @Param("departureDate") LocalDate departureDate);
}
