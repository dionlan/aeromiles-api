package com.aeromiles.service;

import com.aeromiles.azul.dto.ResponseDTO;
import com.aeromiles.azul.dto.TripDTO;
import com.aeromiles.azul.dto.AzulApiResponse;
import com.aeromiles.azul.entity.ResponseData;
import com.aeromiles.azul.entity.Trip;
import com.aeromiles.azul.repository.TripRepository;
import com.aeromiles.configuration.HttpClientConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okhttp3.internal.http2.StreamResetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AzulService {

    private static final Logger logger = LoggerFactory.getLogger(AzulService.class);

    private static final OkHttpClient client = HttpClientConfig.createClient();
    private static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter STD_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private TripRepository tripRepository;

    private String getValidAuthToken() {
        // Implemente a lógica para obter um token válido
        // Pode ser uma chamada a um endpoint de autenticação ou usar um token pré-gerado
        return "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYmYiOjE3NTIyNDY5NDIsImV4cCI6MTc1MjMzMzM0MiwiaXNzIjoiQXp1bCBMaW5oYXMgQWVyZWFzIiwiYXVkIjoiQXJxdWl0ZXR1cmFBenVsIiwidW5pcXVlSWRlbnRpZmllciI6ImFlZTg4Njc0LTVjNGYtNDAzYS1hMGFkLThlZjExZWJmYzc2NSJ9.-EqJh9bq5exh6alkMYJ0SpVuJWZgrbtlDrsLD3MDpXE";
    }

    boolean success = false;
    int attempts = 0;
    private static final int MAX_RETRIES = 5;
    private static final long INITIAL_WAIT_MS = 1000;

    public void searchFlightsAzul(String departureStation, String arrivalStation, String departureDate) {
        final String url = "https://b2c-api.voeazul.com.br/tudoAzulReservationAvailability/api/tudoazul/reservation/availability/v6/availability";

        try {
            // Preparar datas
            LocalDate departureLocalDate = LocalDate.parse(departureDate);
            String stdDate = departureLocalDate.format(API_DATE_FORMATTER);
            String apiDate = departureLocalDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

            // Montar JSON da requisição
            String jsonBody = String.format("""
                {
                    "criteria": [
                        {
                            "departureStation": "%s",
                            "arrivalStation": "%s",
                            "departureDate": "%s",
                            "std": "%s"
                        }
                    ],
                    "flexibleDays": {
                        "daysToLeft": 3,
                        "daysToRight": 3
                    },
                    "passengers": [
                        {
                            "companionPass": false,
                            "count": 1,
                            "type": "ADT"
                        }
                    ],
                    "currencyCode": "BRL"
                }
            """, departureStation, arrivalStation, stdDate, apiDate);

            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

            String authToken = getValidAuthToken(); // 🔐 Este método deve retornar token válido
            Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("accept", "application/json, text/plain, */*")
                .addHeader("accept-language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .addHeader("authorization", authToken)
                .addHeader("content-type", "application/json")
                .addHeader("culture", "pt-BR")
                .addHeader("device", "novosite")
                .addHeader("ocp-apim-subscription-key", "fb38e642c899485e893eb8d0a373cc17")
                .addHeader("origin", "https://www.voeazul.com.br")
                .addHeader("priority", "u=1, i")
                .addHeader("referer", "https://www.voeazul.com.br/")
                .addHeader("sec-ch-ua", "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-site", "same-site")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36")
                .build();

            while (!success && attempts < MAX_RETRIES) {
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        if (response.code() == 403) {
                            System.out.println("⚠ Requisição bloqueada (403) - Tentativa " + attempts);
                        }
                        String errorBody = response.body() != null ? response.body().string() : "No error body";
                        logger.error("❌ Erro na resposta da Azul: {} - {}", response.code(), errorBody);
                        throw new RuntimeException("Erro na requisição: " + response.code() + " - " + errorBody);
                    }

                    String responseBody = response.body().string();
                    AzulApiResponse azulApiResponse = mapper.readValue(responseBody, AzulApiResponse.class);

                    ResponseData responseData = azulApiResponse != null ? azulApiResponse.getData() : null;
                    List<TripDTO> trips = responseData != null ? responseData.getTrips() : Collections.emptyList();

                    if (trips.isEmpty()) {
                        logger.info("ℹ️ Nenhuma viagem encontrada para {}/{} em {}", departureStation, arrivalStation, departureDate);
                        return;
                    }

                    for (TripDTO tripDTO : trips) {
                        try {
                            Trip tripEntity = Trip.fromDTO(tripDTO);
                            tripRepository.save(tripEntity);
                            logger.debug("✅ Viagem salva com sucesso: {}", tripEntity.getId());
                        } catch (Exception e) {
                            logger.error("❌ Erro ao salvar tripDTO: {}", tripDTO, e);
                        }
                    }

                    success = true;
                } catch (StreamResetException e) {
                    logger.error("❌ Falha StreamResetException ao consultar voos Azul", e);
                    throw new RuntimeException("Erro de stream com a Azul", e);

                } catch (IOException e) {
                    logger.error("❌ Falha IO ao consultar a Azul", e);
                    throw new RuntimeException("Erro de comunicação com a Azul", e);

                } catch (Exception e) {
                    logger.error("❌ Erro inesperado ao consultar a Azul", e);
                    throw new RuntimeException("Erro inesperado ao consultar voos", e);
                }
                if (!success) {
                    waitBeforeRetry(attempts);
                }
                attempts++;
            }
        } catch (RuntimeException e) {
            logger.error("❌❌ Erro DE RUNTIME inesperado ao consultar a Azul", e);
            throw new RuntimeException("❌❌Erro inesperado ao consultar voos", e);
        }
    }

    private void waitBeforeRetry(int attempt) {
        try {
            Thread.sleep(INITIAL_WAIT_MS * (long)Math.pow(2, attempt));
        } catch (InterruptedException e) {
            logger.error("❌❌❌ Erro DE INTERRUPTEDEXCEPTION inesperado ao consultar a Azul", e);
            Thread.currentThread().interrupt();
        }
    }
}
