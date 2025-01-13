package com.aeromiles.repository;

import com.aeromiles.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {

    @Query("""
           SELECT a 
           FROM Airport a 
           WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(a.iata) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(a.city) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(a.state) LIKE LOWER(CONCAT('%', :query, '%'))
           """)
    List<Airport> searchByMultipleFields(String query);
}
