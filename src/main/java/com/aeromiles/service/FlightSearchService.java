package com.aeromiles.service;

import com.aeromiles.model.maxmilhas.SearchResponse;
import com.aeromiles.model.maxmilhas.dto.*;
import com.aeromiles.model.maxmilhas.entity.*;
import com.aeromiles.repository.OfferRepository;
import com.aeromiles.util.ResponseParser;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightSearchService {

    @Autowired
    private WebClient webClient;

    @Autowired
    private OfferRepository offerRepository;

    public String searchAirlines() {
        String url = "https://www.maxmilhas.com.br/busca-passagens-aereas/RT/BSB/CGH/2025-01-29/2025-02-04/1/0/0/EC";
        return webClient
            .get()
            .uri(url)
            .retrieve()
            .bodyToMono(String.class)
            .map(this::extractJsonFromHtml) // Usando o método com Jsoup
            .block(); // Bloqueando para obter a resposta síncrona
    }

    public String extractJsonFromHtml(String html) {
        Document doc = Jsoup.parse(html);
        // Localizar o <script id="__NEXT_DATA__" e extrair o conteúdo JSON
        return doc.select("script#__NEXT_DATA__").html();
    }

    // Utilizando ResponseParser para extrair os dados
    public String getSearchIdFromResponse(String response) {
        return ResponseParser.extractSearchId(response);
    }

    // Utilizando ResponseParser para extrair as companhias aéreas
    public List<String> parseAirlines(String response) {
        return ResponseParser.extractAirlines(response);
    }

    public void searchFlightsByAirline(String airline, String searchId) {
        String url = "/search/air-offer/offers/" + searchId + "/" + airline;
        List<OfferDTO> offerDTOs = webClient.get()
            .uri(uriBuilder -> uriBuilder.path(url).build())
            .retrieve()
            .bodyToMono(SearchResponse.class)
            .map(SearchResponse::getOffers)
            .block();

        // Verificando se há ofertas e salvando no banco
        if (offerDTOs != null && !offerDTOs.isEmpty()) {
            //offerRepository.saveAll(offers);
            List<Offer> offers = offerDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

            offerRepository.saveAll(offers);
            System.out.println("Successfully saved " + offers.size() + " offers.");
        } else {
            System.out.println("No offers to save.");
        }

        /*Map<String, Object> responseWc = webClient.get()
            .uri(uriBuilder -> uriBuilder
            .path(url)
            .build(searchId, airline))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        // Extraindo os dados
        Map<String, Object> searchDataWc = (Map<String, Object>) responseWc.get("searchData");

        // Acessando os campos desejados
        String searchIdFromResponse = (String) searchDataWc.get("searchId");
        String journeyType = (String) searchDataWc.get("journeyType");
        String cabin = (String) searchDataWc.get("cabin");

        // Aqui você pode usar os dados obtidos conforme necessário
        System.out.println("Search ID: " + searchIdFromResponse);
        System.out.println("Journey Type: " + journeyType);
        System.out.println("Cabin: " + cabin);

        /*JsonNode items = searchData.path("items");
        for (JsonNode item : items) {
            String originLocationCode = item.path("originLocationCode").asText();
            String destinationLocationCode = item.path("destinationLocationCode").asText();
            String departureDate = item.path("departureDate").asText();
            System.out.println("Origin: " + originLocationCode + ", Destination: " + destinationLocationCode + ", Date: " + departureDate);
        }

        // Exemplo de como acessar as ofertas
        JsonNode offers = rootNode.path("offers");
        for (JsonNode offer : offers) {
            String offerType = offer.path("type").asText();
            JsonNode thirdPartyOffers = offer.path("thirdPartyOffers");
            for (JsonNode thirdPartyOffer : thirdPartyOffers) {
                JsonNode informativeBoundData = thirdPartyOffer.path("informativeBoundData");
                for (JsonNode boundData : informativeBoundData) {
                    int amount = boundData.path("amount").asInt();
                    JsonNode ptcAmount = boundData.path("ptcAmount");
                    int adtAmount = ptcAmount.path("ADT").asInt();
                    System.out.println("Offer Type: " + offerType + ", Amount: " + amount + ", ADT Amount: " + adtAmount);
                }
            }
        }

        // Exibindo valores do searchId
        System.out.println("Search ID: " + searchIdFromResponse);
        System.out.println("Journey Type: " + journeyType);
        System.out.println("Cabin: " + cabin);*/

        //return searchIdFromResponse;*/
    }

    private Offer convertToEntity(OfferDTO dto) {
        Offer offer = new Offer();
        offer.setIdOffer(dto.getIdOffer());
        offer.setComparativeLevel(dto.getComparativeLevel());
        offer.setType(dto.getType());

        List<ThirdPartyOffer> thirdPartyOffers = dto.getThirdPartyOffers()
            .stream()
            .map(this::convertThirdPartyOfferToEntity)
            .collect(Collectors.toList());
        offer.setThirdPartyOffers(thirdPartyOffers);

        List<Bound> bounds = dto.getBounds()
            .stream()
            .map(this::convertBoundToEntity)
            .collect(Collectors.toList());
        offer.setBounds(bounds);
        return offer;
    }

    private ThirdPartyOffer convertThirdPartyOfferToEntity(ThirdPartyOfferDTO dto) {
        ThirdPartyOffer thirdPartyOffer = new ThirdPartyOffer();
        thirdPartyOffer.setProvider(dto.getProvider());
        thirdPartyOffer.setCurrencyCode(dto.getCurrencyCode());
        thirdPartyOffer.setAmount(dto.getAmount());
        return thirdPartyOffer;
    }

    private Bound convertBoundToEntity(BoundDTO dto) {
        Bound bound = new Bound();
        List<Segment> segments = dto.getSegments()
            .stream()
            .map(this::convertSegmentToEntity)
            .collect(Collectors.toList());
        bound.setSegments(segments);

        return bound;
    }

    private Segment convertSegmentToEntity(SegmentDTO dto) {
        Segment segment = new Segment();
        segment.setOperatingFlightNumber(dto.getOperatingFlightNumber());
        segment.setDuration(dto.getDuration());
        segment.setStopQuantity(dto.getStopQuantity());
        segment.setIdSegment(dto.getId());
        segment.setCabin(dto.getCabin());
        segment.setBookingClass(dto.getBookingClass());

        Bound bound = new Bound();
        segment.setBound(bound);

        segment.setDeparture(convertLocationToEntity(dto.getDeparture()));
        segment.setArrival(convertLocationToEntity(dto.getArrival()));

        return segment;
    }

    private Location convertLocationToEntity(LocationDTO dto) {
        Location location = new Location();
        location.setLocationCode(dto.getLocationCode());
        location.setDateTime(dto.getDateTime());
        return location;
    }

    @Transactional
    public void saveOffer(Offer offer) {
        //offerRepository.save(offer);
    }
}
