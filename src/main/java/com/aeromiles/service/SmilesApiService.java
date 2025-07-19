package com.aeromiles.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SmilesApiService {

    private static final String API_KEY = "aJqPU7xNHl9qN3NVZnPaJ208aPo2Bh2p2ZV844tw";
    private static final String BASE_URL = "https://api-air-flightsearch-green.smiles.com.br/v1/airlines/search";
    private final Random random = new Random();
    private CloseableHttpClient httpClient;
    @Autowired
    private ObjectMapper objectMapper;
    private BasicCookieStore cookieStore;

    @PostConstruct
    public void init() {
        this.cookieStore = new BasicCookieStore();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);

        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(Timeout.of(15, TimeUnit.SECONDS))
            .setResponseTimeout(Timeout.of(30, TimeUnit.SECONDS))
            .build();

        this.httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .setDefaultCookieStore(cookieStore)
            .build();

        refreshCookies();
    }

    public Optional<JsonNode> searchFlights(String origin, String destination, String date) throws Exception {
        try {
            simulateBrowserBehavior();

            Map<String, String> params = new LinkedHashMap<>();
            params.put("cabin", "ECONOMIC");
            params.put("originAirportCode", origin);
            params.put("destinationAirportCode", destination);
            params.put("departureDate", date);
            params.put("memberNumber", "");
            params.put("adults", "1");
            params.put("children", "0");
            params.put("infants", "0");
            params.put("forceCongener", "false");
            params.put("cookies", "_gid=undefined;");
            params.put("_", String.valueOf(Instant.now().toEpochMilli()));

            HttpGet httpGet = createHttpGet(BASE_URL, params);

            try (CloseableHttpResponse response = httpClient.execute(httpGet, HttpClientContext.create())) {
                if (response.getCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    JsonNode rootNode = objectMapper.readTree(responseBody);

                    //System.out.println("\n📄 Raw API Response (formatted JSON):");
                    //System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));

                    return Optional.of(rootNode);
                } else if (response.getCode() == 406) {
                    System.out.println("⚠️ Received 406 - Refreshing cookies and retrying...");
                    refreshCookies();
                    addSecurityHeaders(httpGet);
                    try (CloseableHttpResponse retryResponse = httpClient.execute(httpGet, HttpClientContext.create())) {
                        if (retryResponse.getCode() == 200) {
                            String responseBody = EntityUtils.toString(retryResponse.getEntity());
                            JsonNode rootNode = objectMapper.readTree(responseBody);

                            // Print formatted JSON for retry
                            //System.out.println("\n📄 Retry API Response (formatted JSON):");
                            //System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));

                            return Optional.of(rootNode);
                        }
                    }
                } else {
                    System.err.println("❌ API returned status code: " + response.getCode());
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.err.println("Resposta: " + responseBody);
                }
                return Optional.empty();
            }
        } catch (HttpClientErrorException e) {
            System.out.println("\n❌ Erro na requisição: " + e.getStatusCode());
            System.out.println("Resposta: " + e.getResponseBodyAsString());
            return Optional.empty();

        } catch (Exception e) {
            System.out.println("❌ Exception during flight search:");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private HttpGet createHttpGet(String url, Map<String, String> params) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);
        params.forEach(uriBuilder::addParameter);

        HttpGet httpGet = new HttpGet(uriBuilder.build());
        setDefaultHeaders(httpGet);
        return httpGet;
    }

    private void simulateBrowserBehavior() throws IOException, InterruptedException {
        // Initial access
        executeSimpleGet("https://www.smiles.com.br");

        // Secondary requests
        List<String> endpoints = List.of("/api/config", "/api/session");
        for (String endpoint : endpoints) {
            executeSimpleGet("https://www.smiles.com.br" + endpoint);
            Thread.sleep(random.nextInt(1000) + 500); // 0.5-1.5s delay
        }
    }

    private void executeSimpleGet(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(HttpHeaders.USER_AGENT, generateUserAgent());
        httpGet.setHeader(HttpHeaders.REFERER, "https://www.smiles.com.br/");
        httpClient.execute(httpGet).close();
    }

    private void refreshCookies() {
        this.cookieStore.clear();

        // Forma correta usando BasicClientCookie do pacote correto
        BasicClientCookie cookie1 = new BasicClientCookie("bm_sv", generateBmSv());
        cookie1.setDomain("smiles.com.br");
        cookie1.setPath("/");

        BasicClientCookie cookie2 = new BasicClientCookie("bm_sz", generateAbck());
        cookie2.setDomain("smiles.com.br");
        cookie2.setPath("/");

        BasicClientCookie cookie3 = new BasicClientCookie("_abck", generateAbck());
        cookie2.setDomain("smiles.com.br");
        cookie2.setPath("/");

        BasicClientCookie cookie4 = new BasicClientCookie("ak_bmsc", generateAbck());
        cookie2.setDomain("smiles.com.br");
        cookie2.setPath("/");

        cookieStore.addCookie(cookie1);
        cookieStore.addCookie(cookie2);
        cookieStore.addCookie(cookie3);
        cookieStore.addCookie(cookie4);
    }

    private void setDefaultHeaders(HttpGet httpGet) {
        httpGet.setHeader("Accept", "application/json, text/plain, */*");
        httpGet.setHeader("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7");
        httpGet.setHeader("Cache-Control", "no-cache");
        httpGet.setHeader("Origin", "https://www.smiles.com.br");
        httpGet.setHeader("Referer", "https://www.smiles.com.br/");
        httpGet.setHeader("sec-ch-ua", "\"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"138\", \"Google Chrome\";v=\"138\"");
        httpGet.setHeader("sec-ch-ua-mobile", "?0");
        httpGet.setHeader("sec-ch-ua-platform", "\"Windows\"");
        httpGet.setHeader("sec-fetch-dest", "empty");
        httpGet.setHeader("sec-fetch-mode", "cors");
        httpGet.setHeader("sec-fetch-site", "same-site");
        httpGet.setHeader("User-Agent", generateUserAgent());
        httpGet.setHeader("x-api-key", API_KEY);
        httpGet.setHeader("x-request-id", generateRequestId());
        httpGet.setHeader("x-forwarded-for", generateRandomIp());
    }

    private void addSecurityHeaders(HttpGet httpGet) {
        httpGet.setHeader("x-bm-srv", generateHash("MD5", String.valueOf(System.currentTimeMillis())));
        httpGet.setHeader("x-defender-id", generateHash("MD5", String.valueOf(System.currentTimeMillis())));
    }

    private String generateUserAgent() {
        String[][] chromeVersions = {
            {"138.0.0.0", "537.36"},
            {"137.0.0.0", "537.36"},
            {"136.0.0.0", "537.36"}
        };
        String[] version = chromeVersions[random.nextInt(chromeVersions.length)];
        return String.format("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/%s (KHTML, like Gecko) Chrome/%s Safari/%s",
            version[1], version[0], version[1]);
    }

    private String generateRequestId() {
        return generateHash("MD5", System.currentTimeMillis() + "" + random.nextInt(90000) + 10000);
    }

    private String generateRandomIp() {
        return String.format("%d.%d.%d.%d",
            random.nextInt(245) + 11,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256));
    }

    private String generateBmSv() {
        String part1 = generateHash("SHA-256", String.valueOf(System.currentTimeMillis())).substring(0, 32).toUpperCase();
        return part1 + "~1";
    }

    private String generateBmSz() {
        String part1 = generateHash("SHA-256", String.valueOf(System.currentTimeMillis())).substring(0, 32).toUpperCase();
        return part1 + "~4342326~4276529";
    }

    private String generateAbck() {
        String part1 = generateHash("MD5", String.valueOf(random.nextInt(100000))).substring(0, 16).toUpperCase();
        return String.format("%s~-1~YAAQ%d~-1~-1~-1", part1, random.nextInt(9000) + 1000);
    }

    private String generateAkBmsc() {
        String part1 = generateHash("MD5", String.valueOf(System.currentTimeMillis())).substring(0, 32).toUpperCase();
        return part1 + "~000000000000000000000000000000~YAAQ...";
    }

    private String generateHash(String algorithm, String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate hash", e);
        }
    }
}
