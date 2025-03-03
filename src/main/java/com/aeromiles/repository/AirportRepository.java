package com.aeromiles.repository;

import com.aeromiles.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {

    @Query("""
           SELECT a 
           FROM Airport a 
           WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(a.code) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(a.city) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(a.state) LIKE LOWER(CONCAT('%', :query, '%'))
           """)
    List<Airport> searchByMultipleFields(String query);

    Optional<Airport> findByCode(String code);
}
