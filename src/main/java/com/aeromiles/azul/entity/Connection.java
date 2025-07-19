package com.aeromiles.azul.entity;

import com.aeromiles.azul.dto.ConnectionsDTO;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@Getter
@Setter
public class Connection {

    private int count;

    @ElementCollection
    @CollectionTable(name = "station_information", joinColumns = @JoinColumn(name = "journey_identifier_id"))
    private List<StationInformation> stationsInformation = new ArrayList<>();

    public static Connection fromDTO(ConnectionsDTO dto) {
        if (dto == null) return null;

        Connection connection = new Connection();
        connection.setCount(dto.getCount());

        if (dto.getStationsInformation() != null) {
            connection.setStationsInformation(
                    dto.getStationsInformation().stream()
                            .map(StationInformation::fromDTO)
                            .collect(Collectors.toList())
            );
        }

        return connection;
    }
}
