package com.aeromiles.model.maxmilhas;

import com.aeromiles.model.maxmilhas.dto.OfferDTO;
import lombok.Data;

import java.util.List;

@Data
public class SearchResponse {

    private List<OfferDTO> offers;

}
