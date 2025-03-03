package com.aeromiles.model.onetwothree;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class AirlineOneTwoThree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    private String codeCia;

    private String airlineLogo;

}
