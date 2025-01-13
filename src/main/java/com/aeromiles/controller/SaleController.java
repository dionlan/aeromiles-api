package com.aeromiles.controller;

import com.aeromiles.model.Sale;
import com.aeromiles.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired
    private SalesService salesService;

    @GetMapping("")
    public List<Sale> getSales() {
        return salesService.getSales();
    }

    @GetMapping("/error")
    public String error() {
        return "An error occurred!";
    }
}   
