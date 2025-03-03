package com.aeromiles.service;

import com.aeromiles.model.Airport;
import com.aeromiles.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AirportService {

    private static final Map<String, Airport> AIRPORTS = new HashMap<>();

    @Autowired
    private AirportRepository airportRepository;

    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public List<Airport> searchAirports(String query) {
        return airportRepository.searchByMultipleFields(query);
    }

    static {
        AIRPORTS.put("THE", new Airport("THE", "Senador Petrônio Portella", "Piauí", "Teresina", "Brasil"));
        AIRPORTS.put("SLZ", new Airport("SLZ", "Marechal Cunha Machado", "Maranhão", "São Luís", "Brasil"));
        AIRPORTS.put("CGB", new Airport("CGB", "Marechal Rondon", "Mato Grosso", "Cuiabá", "Brasil"));
        AIRPORTS.put("CGR", new Airport("CGR", "Campo Grande", "Mato Grosso do Sul", "Campo Grande", "Brasil"));
        AIRPORTS.put("PMW", new Airport("PMW", "Brigadeiro Lysias Rodrigues", "Tocantins", "Palmas", "Brasil"));
        AIRPORTS.put("RBR", new Airport("RBR", "Plácido de Castro", "Acre", "Rio Branco", "Brasil"));
        AIRPORTS.put("BVB", new Airport("BVB", "Atlas Brasil Cantanhede", "Roraima", "Boa Vista", "Brasil"));
        AIRPORTS.put("MCP", new Airport("MCP", "Alberto Alcolumbre", "Amapá", "Macapá", "Brasil"));
        AIRPORTS.put("PVH", new Airport("PVH", "Jorge Teixeira de Oliveira", "Rondônia", "Porto Velho", "Brasil"));
        AIRPORTS.put("FLN", new Airport("FLN", "Hercílio Luz", "Santa Catarina", "Florianópolis", "Brasil"));
        AIRPORTS.put("VCP", new Airport("VCP", "Viracopos", "São Paulo", "São Paulo", "Brasil"));
        AIRPORTS.put("GRU", new Airport("GRU", "Guarulhos", "São Paulo", "São Paulo", "Brasil"));
        AIRPORTS.put("CGH", new Airport("CGH", "Congonhas", "São Paulo", "São Paulo", "Brasil"));
        AIRPORTS.put("GIG", new Airport("GIG", "Galeão", "Rio de Janeiro", "Rio de Janeiro", "Brasil"));
        AIRPORTS.put("SDU", new Airport("SDU", "Santos Dumont", "Rio de Janeiro", "Rio de Janeiro", "Brasil"));
        AIRPORTS.put("SSA", new Airport("SSA", "Luís Eduardo Magalhães", "Bahia", "Salvador", "Brasil"));
        AIRPORTS.put("FOR", new Airport("FOR", "Pinto Martins", "Ceará", "Fortaleza", "Brasil"));
        AIRPORTS.put("CNF", new Airport("CNF", "Confins", "Minas Gerais", "Belo Horizonte", "Brasil"));
        AIRPORTS.put("PLU", new Airport("PLU", "Pampulha", "Minas Gerais", "Belo Horizonte", "Brasil"));
        AIRPORTS.put("CWB", new Airport("CWB", "Afonso Pena", "Paraná", "Curitiba", "Brasil"));
        AIRPORTS.put("REC", new Airport("REC", "Guararapes", "Pernambuco", "Recife", "Brasil"));
        AIRPORTS.put("MAO", new Airport("MAO", "Eduardo Gomes", "Amazonas", "Manaus", "Brasil"));
        AIRPORTS.put("NAT", new Airport("NAT", "Aluízio Alves", "Rio Grande do Norte", "Natal", "Brasil"));
        AIRPORTS.put("POA", new Airport("POA", "Salgado Filho", "Rio Grande do Sul", "Porto Alegre", "Brasil"));
        AIRPORTS.put("BEL", new Airport("BEL", "Val de Cans", "Pará", "Belém", "Brasil"));
        AIRPORTS.put("GYN", new Airport("GYN", "Santa Genoveva", "Goiás", "Goiânia", "Brasil"));
        AIRPORTS.put("AJU", new Airport("AJU", "Santa Maria", "Sergipe", "Aracaju", "Brasil"));
        AIRPORTS.put("MCZ", new Airport("MCZ", "Zumbi dos Palmares", "Alagoas", "Maceió", "Brasil"));
        AIRPORTS.put("VIX", new Airport("VIX", "Eurico de Aguiar Salles", "Espírito Santo", "Belo Horizonte", "Brasil"));
        AIRPORTS.put("BSB", new Airport("BSB", "Juscelino Kubitschek", "Distrito Federal", "Brasília", "Brasil"));
        AIRPORTS.put("MIA", new Airport("MIA", "Miami", "Florida", "Miami", "United States"));
        AIRPORTS.put("FLL", new Airport("FLL", "Fort Lauderdale-Hollywood", "Florida", "Fort Lauderdale", "United States"));
        AIRPORTS.put("MCO", new Airport("MCO", "Orlando", "Florida", "Orlando", "United States"));
        AIRPORTS.put("IGU", new Airport("IGU", "Foz do Iguaçu", "Paraná", "Foz do Iguaçu", "Brasil"));

    }

    public static Airport getAirport(String code) {
        return AIRPORTS.getOrDefault(code, null);
    }

}
