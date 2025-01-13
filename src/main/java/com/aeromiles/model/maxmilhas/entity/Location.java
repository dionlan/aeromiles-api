package com.aeromiles.model.maxmilhas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Embeddable
public class Location {

    private String locationCode;
    private ZonedDateTime dateTime;
    private String terminal;

}
