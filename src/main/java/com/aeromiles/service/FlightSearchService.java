package com.aeromiles.service;

import com.aeromiles.model.maxmilhas.SearchResponse;
import com.aeromiles.model.maxmilhas.dto.*;
import com.aeromiles.model.maxmilhas.entity.*;
import com.aeromiles.model.onetwothree.FlightOneTwoThree;
import com.aeromiles.model.onetwothree.converter.FlightOneTwoThreeConverter;
import com.aeromiles.model.onetwothree.dto.FlightOneTwoThreeDTO;
import com.aeromiles.model.onetwothree.dto.FlightOneTwoThreeResponseDTO;
import com.aeromiles.model.onetwothree.dto.ResponseDTO;
import com.aeromiles.repository.FlightOneTwoThreeRepository;
import com.aeromiles.repository.OfferRepository;
import com.aeromiles.util.DateUtil;
import com.aeromiles.util.ResponseParser;
import com.aeromiles.util.StringUtil;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightSearchService {

    @Autowired
    private FlightOneTwoThreeRepository flightOneTwoThreeRepository;

    @Autowired
    private WebClient webClient;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private OneTwoThreeService oneTwoThreeService;

    private FlightOneTwoThreeConverter flightOneTwoThreeConverter = new FlightOneTwoThreeConverter();
    private static final String FLIGHT_SEARCH_BASE_URL = "https://www.maxmilhas.com.br/busca-passagens-aereas";
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

    public String searchAirlines(String origem, String destino, String dataPartida, int quantidadeAdultos){
        String url = String.format("%s/OW/%s/%s/%s/%d/0/0/EC", FLIGHT_SEARCH_BASE_URL, origem, destino, dataPartida, quantidadeAdultos);
        return webClient
            .get()
            .uri(url)
            .retrieve()
            .bodyToMono(String.class)
            .map(this::extractJsonFromHtml)
            .block();
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
            .timeout(Duration.ofSeconds(30)) // Define um timeout de 10 segundos
            .retry(3)
            .map(SearchResponse::getOffers)
            .block();

        if (offerDTOs != null && !offerDTOs.isEmpty()) {
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
        offer.setCia(StringUtil.extractAirlineName(dto.getIdOffer()));

        List<ThirdPartyOffer> thirdPartyOffers = dto.getThirdPartyOffers()
            .stream()
            .map(thirdPartyOfferDTO -> {
                ThirdPartyOffer thirdPartyOffer = convertThirdPartyOfferToEntity(thirdPartyOfferDTO);
                thirdPartyOffer.setOffer(offer);
                return thirdPartyOffer;
            })
            .collect(Collectors.toList());
        offer.setThirdPartyOffers(thirdPartyOffers);

        List<Bound> bounds = dto.getBounds()
            .stream()
            .map(this::convertBoundToEntity)
            .collect(Collectors.toList());
        offer.setBounds(bounds);

        bounds.forEach(bound -> bound.setOffer(offer));

        if (dto.getPriceDetails() != null) {
            PriceDetails priceDetails = convertPriceDetailsToEntity(dto.getPriceDetails());
            offer.setPriceDetails(priceDetails);
        }

        return offer;
    }

    private PriceDetails convertPriceDetailsToEntity(PriceDetailsDTO dto) {
        PriceDetails priceDetails = new PriceDetails();
        priceDetails.setTotalPrices(convertTotalPricesToEntity(dto.getTotalPrices()));
        return priceDetails;
    }

    private TotalPrices convertTotalPricesToEntity(TotalPricesDTO dto) {
        TotalPrices totalPrices = new TotalPrices();
        totalPrices.setBase(dto.getBase());
        totalPrices.setTotal(dto.getTotal());
        totalPrices.setCurrencyCode(dto.getCurrencyCode());
        totalPrices.setTotalTaxes(dto.getTotalTaxes());
        totalPrices.setTotalFees(dto.getTotalFees());
        totalPrices.setTotalDiscounts(dto.getTotalDiscounts());
        return totalPrices;
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
        bound.setCarrier(dto.getCarrier());
        bound.setValidatedBy(dto.getValidatedBy());
        bound.setDuration(dto.getDuration());
        bound.setDaysDifference(dto.getDaysDifference());
        bound.setFareProfile(convertFareProfileToEntity(dto.getFareProfile()));
        bound.setDeparture(convertLocationToEntity(dto.getDeparture()));
        bound.setArrival(convertLocationToEntity(dto.getArrival()));
        bound.setTotalStops(dto.getTotalStops());
        bound.setHasCheckedBags(dto.isHasCheckedBags());
        bound.setHasCarryOnBags(dto.isHasCarryOnBags());

        List<Segment> segments = dto.getSegments()
            .stream()
            .map(segmentDTO -> convertSegmentToEntity(segmentDTO, bound))
            .collect(Collectors.toList());
        bound.setSegments(segments);

        return bound;
    }

    private FareProfile convertFareProfileToEntity(FareProfileDTO dto) {
        if (dto == null) {
            return null;
        }
        FareProfile fareProfile = new FareProfile();
        fareProfile.setBaggageAllowance(dto.getBaggageAllowance());
        fareProfile.setFareRules(dto.getFareRules());
        fareProfile.setMarketingName(dto.getMarketingName());
        return fareProfile;
    }


    private Segment convertSegmentToEntity(SegmentDTO dto, Bound bound) {
        Segment segment = new Segment();
        segment.setMarketingAirlineCode(dto.getMarketingAirlineCode());
        segment.setOperatingAirlineCode(dto.getOperatingAirlineCode());
        segment.setMarketingFlightNumber(dto.getMarketingFlightNumber());
        segment.setOperatingFlightNumber(dto.getOperatingFlightNumber());
        segment.setDuration(dto.getDuration());
        segment.setStopQuantity(dto.getStopQuantity());
        //.setEquipment(dto.getEquipment());
        segment.setIdSegment(dto.getIdSegment());
        segment.setGroundOperationalInfo(convertGroundOperationalInfoToEntity(dto.getGroundOperationalInfo()));
        segment.setCabin(dto.getCabin());
        segment.setBookingClass(dto.getBookingClass());
        segment.setFareClass(dto.getFareClass());
        segment.setFareBasis(dto.getFareBasis());
        segment.setBound(bound); // Relaciona o Segment ao Bound atual

        segment.setDeparture(convertLocationToEntity(dto.getDeparture()));
        segment.setArrival(convertLocationToEntity(dto.getArrival()));

        return segment;
    }

    private Location convertLocationToEntity(LocationDTO dto) {
        if (dto == null) {
            return null;
        }
        Location location = new Location();
        location.setLocationCode(dto.getLocationCode());
        try {
            location.setDateTime(ZonedDateTime.parse(dto.getDateTime()));
        } catch (Exception e) {
            System.err.println("Failed to parse ZonedDateTime: " + e.getMessage());
        }
        location.setTerminal(dto.getTerminal());
        return location;
    }

    private GroundOperationalInfo convertGroundOperationalInfoToEntity(GroundOperationalInfoDTO dto) {
        if (dto == null) {
            return null;
        }
        GroundOperationalInfo info = new GroundOperationalInfo();
        info.setType(dto.getType());
        info.setDuration(dto.getDuration());
        info.setHasChangeOfAirportAfter(dto.isHasChangeOfAirportAfter());
        info.setNextBoardingPoint(dto.getNextBoardingPoint());
        return info;
    }

    @Transactional
    public void saveOffer(Offer offer) {
        //offerRepository.save(offer);
    }


    public String searchFlightOneTwoThree(String codeFrom, String codeTo, String dateOutbound, String dateInbound) {
        String url = "https://123milhas.com/v2/busca";

        // Defina os parâmetros de consulta
        String fullUrl = String.format("%s?de=BSB&para=CGH&ida=29-01-2025&volta=04-02-2025&adultos=1&criancas=0&bebes=0&classe=3&is_loyalty=1", url);

        // Realizando a requisição
        String responseMono = webClient.get()
            .uri(fullUrl)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
            .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
            .header("accept-encoding", "gzip, deflate, br, zstd")
            .header("Cache-Control", "no-cache")
            .header("Connection", "keep-alive")
            .header("Referer", "https://123milhas.com/")
            .header("Sec-Fetch-Dest", "document")
            .header("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"")
            .header("sec-ch-ua-platform", "\"Windows\"")
            .header("Sec-Fetch-Mode", "navigate")
            .header("Sec-Fetch-Site", "same-origin")
            .header("Sec-Fetch-User", "?1")
            .header("Upgrade-Insecure-Requests", "1")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
            .retrieve()
            .bodyToMono(String.class)
            .doOnNext(body -> {
                // Imprime os dados conforme são retornados
                System.out.println("Partial Response: " + body);
            }).block();

        return responseMono;
    }

    public ResponseDTO search(String fromCity, String toCity, String departureDate, boolean consultaExterna) {

        if(consultaExterna){
            return oneTwoThreeService.searchFlightsExterna(fromCity, toCity, departureDate, 1, 0, 0, 3, 1);
        }

        LocalDate departureDateLocalDate = DateUtil.stringToLocalDate(departureDate);

        List<FlightOneTwoThree> flights = flightOneTwoThreeRepository.findFlightsByLocationsAndDate(fromCity, toCity, departureDateLocalDate);

        List<FlightOneTwoThreeDTO> flightsResponse = flights.stream()
            .map(flightOneTwoThreeConverter::convertToDTO)
            .collect(Collectors.toList());

        FlightOneTwoThreeResponseDTO response = new FlightOneTwoThreeResponseDTO();
        response.setFlights(flightsResponse.stream().map(flightOneTwoThreeConverter::convertToDTOResponse).collect(Collectors.toList()));
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setFlights(response.getFlights());
        return responseDTO;
    }
}
