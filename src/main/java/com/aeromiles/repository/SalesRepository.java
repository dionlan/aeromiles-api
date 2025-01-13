package com.aeromiles.repository;

import com.aeromiles.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesRepository extends JpaRepository<Sale, Long> {}
