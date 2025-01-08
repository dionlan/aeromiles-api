package com.aeromiles.service;

import com.aeromiles.model.FlightFilter;
import com.aeromiles.util.ResponseParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Service
public class FlightSearchService {

    private static final String BASE_URL = "https://bff-site.maxmilhas.com.br";
    private static final HttpHeaders HEADERS;

    static {
        HEADERS = new HttpHeaders();
        HEADERS.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");
        HEADERS.add("accept", "application/json, text/plain, */*");
        HEADERS.add("accept-language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7");
        HEADERS.add("origin", "https://www.maxmilhas.com.br");
        HEADERS.add("priority", "u=1, i");
        HEADERS.add("referer", "https://www.maxmilhas.com.br/");
        HEADERS.add("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"");
        HEADERS.add("sec-ch-ua-mobile", "?0");
        HEADERS.add("sec-ch-ua-platform", "\"Windows\"");
        HEADERS.add("sec-fetch-dest", "empty");
        HEADERS.add("sec-fetch-mode", "cors");
        HEADERS.add("sec-fetch-site", "same-site");
    }

    private final RestTemplate restTemplate;

    public FlightSearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String searchAirlines() throws IOException {
        String url = "https://www.maxmilhas.com.br/busca-passagens-aereas/RT/BSB/CGH/2025-01-29/2025-02-04/1/0/0/EC";
        HttpEntity<Void> request = new HttpEntity<>(HEADERS);

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, request, byte[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            byte[] body = response.getBody();
            // Verifique se a resposta está compactada
            if (isGzipped(body)) {
                String responseHtml = decompressGzip(body);
                String json = extractJsonFromHtml(responseHtml);
                return json;
            } else {
                return new String(body); // Assumindo que já é texto
            }
        } else {
            throw new RuntimeException("Erro ao chamar a API: " + response.getStatusCode());
        }
    }

    public String extractJsonFromHtml(String html) {
        Document doc = Jsoup.parse(html);
        // Localizar o <script id="__NEXT_DATA__" e extrair o conteúdo JSON
        return doc.select("script#__NEXT_DATA__").html();
    }

    private boolean isGzipped(byte[] data) {
        return data.length > 2 && (data[0] == (byte) 0x1F && data[1] == (byte) 0x8B);
    }

    private String decompressGzip(byte[] data) throws IOException {
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data));
             BufferedReader reader = new BufferedReader(new InputStreamReader(gis))) {
            StringBuilder outStr = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                outStr.append(line);
            }
            return outStr.toString();
        }
    }

    // Utilizando ResponseParser para extrair os dados
    public String getSearchIdFromResponse(String response) {
        return ResponseParser.extractSearchId(response);
    }

    // Utilizando ResponseParser para extrair as companhias aéreas
    public List<String> parseAirlines(String response) {
        return ResponseParser.extractAirlines(response);
    }

    public String searchFlightsByAirline(String airline, String searchId) throws IOException {
        String url = BASE_URL + "/search/air-offer/offers/" + searchId + "/" + airline;
        HttpEntity<Void> request = new HttpEntity<>(HEADERS);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        // Utilizando ObjectMapper para mapear a resposta JSON
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());

        // Acessando os campos desejados
        JsonNode searchData = rootNode.path("searchData");
        String searchIdFromResponse = searchData.path("searchId").asText();
        String journeyType = searchData.path("journeyType").asText();
        String cabin = searchData.path("cabin").asText();

        JsonNode items = searchData.path("items");
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
        System.out.println("Cabin: " + cabin);

        return searchIdFromResponse;
    }
}
