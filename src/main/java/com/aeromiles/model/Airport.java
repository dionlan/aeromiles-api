package com.aeromiles.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Airport {

    @Id
    @Column(nullable = false, unique = true)
    private String code;

    private String name;

    private String state;

    private String city;

    private String country;
}
