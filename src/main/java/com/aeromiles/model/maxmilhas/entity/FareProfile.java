package com.aeromiles.model.maxmilhas.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class FareProfile {

    private String marketingName;

}
