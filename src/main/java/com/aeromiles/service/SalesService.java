package com.aeromiles.service;

import com.aeromiles.model.Sale;
import com.aeromiles.repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesService {

    @Autowired
    private SalesRepository salesRepository;

    public List<Sale> getSales() {
        return salesRepository.findAll();
    }
}
