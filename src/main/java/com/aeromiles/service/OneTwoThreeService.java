package com.aeromiles.service;

import com.aeromiles.configuration.HttpClientConfig;
import com.aeromiles.model.onetwothree.FlightOneTwoThree;
import com.aeromiles.model.onetwothree.Search;
import com.aeromiles.model.onetwothree.converter.FlightOneTwoThreeConverter;
import com.aeromiles.model.onetwothree.dto.*;
import com.aeromiles.repository.SearchRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.DevToolsException;
import org.openqa.selenium.devtools.v131.network.Network;
import org.openqa.selenium.devtools.v131.network.model.Response;
import org.openqa.selenium.devtools.v131.network.model.ResponseReceived;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Service
public class OneTwoThreeService {

    private static final Logger logger = LoggerFactory.getLogger(OneTwoThreeService.class);

    @Autowired
    private SearchRepository searchRepository;
    private static final String API_URL_TEMPLATE = "https://123milhas.com/api/v3/flight/search?iata_from=%s&iata_to=%s&date_outbound=%s&adults=%d&children=%d&babies=%d&class_service=%d&is_loyalty=%d";
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<FlightOneTwoThree> flights = new ArrayList<>();

    private FlightOneTwoThreeConverter flightOneTwoThreeConverter = new FlightOneTwoThreeConverter();

    public List<FlightOneTwoThree> save(String departureAirport, String arrivalAirport, String departureTime,
                                        int adults, int children, int babies, int classType, int isLoyalty) {

        String url = String.format(API_URL_TEMPLATE, departureAirport, arrivalAirport, departureTime, adults, children, babies, classType, isLoyalty);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu", "--no-sandbox", "--window-size=600,400");
        //options.addArguments("--disable-background-timer-throttling", "--disable-backgrounding-occluded-windows");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\dius_\\Downloads\\selenium 131\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver(options);
        //driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        //driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            DevTools devTools = ((ChromeDriver) driver).getDevTools();
            devTools.createSession();

            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.loading-search-container")));

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div#searchResultGroup.search-result__container.search-result.group.search-result__flights-container")));

            /*WebElement flightsDiv = driver.findElement(By.cssSelector("div.flights"));
            //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            //wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.flights")));
            if (!PageLoadHelper.waitForElement(driver, flightsDiv)) {
                System.err.println("Elemento esperado não encontrado. Abortando execução.");
                return flights;
            }*/

            final String targetUrl = "https://123milhas.com/api/v3/flight/search";
            CompletableFuture<Void> jsonReadyFuture = new CompletableFuture<>();
            AtomicBoolean jsonProcessed = new AtomicBoolean(false);

            devTools.addListener(Network.responseReceived(), response -> {
                handleResponse(response, targetUrl, devTools, jsonReadyFuture, jsonProcessed);
            });
            //driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            // Aguarda o processamento do JSON

            waitForJsonProcessing(jsonReadyFuture);

            System.out.println("JSON tratado e salvo no banco com sucesso.");

        } catch (Exception e) {
            System.err.println("Erro ao executar o método save: " + e.getMessage());
        } finally {
            driver.quit(); // Encerra o WebDriver no final de todo o processo
        }

        return flights;
    }

    private void setupDevTools(DevTools devTools) {
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.send(Network.setBlockedURLs(Arrays.asList(
            "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp",
            "*.woff", "*.woff2", "*.ttf", "*.svg",
            "*.mp4", "*.avi", "*.mov", "*.mkv", "*.flv", "*.webm",
            "*.mp3", "*.wav", "*.ogg"
        )));

        //devTools.send(Network.setCacheDisabled(true));
    }

    private void handleResponse(ResponseReceived response, String targetUrl, DevTools devTools, CompletableFuture<Void> jsonReadyFuture, AtomicBoolean jsonProcessed) {
        Response res = response.getResponse();

        if (res.getUrl().contains(targetUrl) && res.getStatus() == 200) {
            System.out.println("Interceptando resposta do endpoint: " + res.getUrl());

            try {
                if (response.getRequestId() != null) {
                    Network.GetResponseBodyResponse responseBody = devTools.send(Network.getResponseBody(response.getRequestId()));
                    String json = responseBody.getBody();

                    if (json != null && !json.isEmpty()) {
                        processResponse(json);
                        jsonProcessed.set(true);
                        jsonReadyFuture.complete(null);
                    } else {
                        System.err.println("O corpo da resposta está vazio ou nulo.");
                    }
                } else {
                    System.err.println("Request ID inválido ou nulo para a resposta do endpoint: " + res.getUrl());
                }
            } catch (DevToolsException e) {
                if (e.getMessage().contains("No data found for resource with given identifier")) {
                    System.err.println("Os dados da resposta não estão mais disponíveis.");
                } else {
                    System.err.println("Erro no DevTools ao obter o corpo da resposta: " + e.getMessage());
                }
                jsonReadyFuture.completeExceptionally(e);
            } catch (Exception e) {
                System.err.println("Erro ao interceptar a resposta: " + e.getMessage());
            }
        }
    }

    private void waitForJsonProcessing(CompletableFuture<Void> jsonReadyFuture) {
        try {
            jsonReadyFuture.get();
        } catch (Exception e) {
            System.err.println("Erro ao aguardar o processamento do JSON: " + e.getMessage());
        }
    }

    /*private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(Duration.ofSeconds(10)) // 🔹 Tempo máximo para conectar
        .readTimeout(Duration.ofSeconds(30)) // 🔹 Tempo máximo de espera pela resposta
        .writeTimeout(Duration.ofSeconds(30)) // 🔹 Tempo máximo de envio de dados
        .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES)) // Pool de 5 conexões
        .build();*/

    private static final OkHttpClient client = HttpClientConfig.createClient();

    public void processResponse(String responseContent) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            RootData rootData = objectMapper.readValue(responseContent, RootData.class);
            flights.addAll(rootData.getData().toEntityList());

            Search search = SearchDTO.toEntity(rootData.getData());
            searchRepository.save(search);
            System.out.println("====================***************************DADOS PROCESSADOS E PERSISTIDOS COM SUCESSO!!!!!!!!!!!!!!!!!!!!!!!!!");
        } catch (Exception e) {
            System.err.println("ERRO AO PROCESSA RESPOSTA!!!!: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static final int MAX_RETRIES = 5;

    public void searchFlights(String departureAirport, String arrivalAirport, String departureDate,
                              Integer adults, Integer children, Integer babies,
                              Integer classService, Integer isLoyalty) {
        String API_URL = String.format(API_URL_TEMPLATE, departureAirport, arrivalAirport, departureDate, adults,
                children, babies, classService, isLoyalty);

        String authorizationToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6Ijg2MjNlOWVjZmUxN2E3Mzc1M2MzODJhNzdjYjFmZjMzNjg4NzdlN2IwNjQzYzM4MDkzMDYwNTU5NzA0ODdmYjUxZTE2MjE0ZWRkMmE1ZjFlIn0.eyJhdWQiOiIzIiwianRpIjoiODYyM2U5ZWNmZTE3YTczNzUzYzM4MmE3N2NiMWZmMzM2ODg3N2U3YjA2NDNjMzgwOTMwNjA1NTk3MDQ4N2ZiNTFlMTYyMTRlZGQyYTVmMWUiLCJpYXQiOjE3NTIyNDY5MzUsIm5iZiI6MTc1MjI0NjkzNSwiZXhwIjoxNzUyMzMzMzM1LCJzdWIiOiIiLCJzY29wZXMiOltdfQ.G-7j4uRVd8U_mgixQ3fkWhHkAW_3I81JMcVx5jcJO_uoebG6LLHwed-KM1eTsMTKQq7dkaT2BeMqoijeurR3lcnNoiPs8W6ndPK0YRo7_jXA1S2VQC89JwP3ieaPkFx8yX5MoIL3y4DxarcV-G62XPFKbRCTyQ5b2vDgBTofmlYt4p4wBozqdEdLeDidL3RpsU-dCmh5omHj6HZzPVntzbUjuQwvpa_SjOMCzEWS2SWIAAvUa-CGaS9plzfxlletmdUYDaajsiA7YltjAtgV1MBP_fxuVKIzJfEpQ3S_ZPBdTbLmPHyAx2moOfJmYmxxbKeXM7S4nv6YqWRDFMXtd3xke6AWqP4SBo6q6pFrbexHDRkVCZ7fjryjIdw-2WfUkIxB5GUezJ_pZ2xcTQVPEhOIVlTBXQT4V6rBQ40TluUnrtpyeetMeK3sfjm1CPsUWS8zCuYHYzDnsl0GyNug8P_JeYk38BM_hv5W_PoJR2sLqZKf2VLniS5jbd9QJKPzMTTmh-616hUADcryk33-5k260MTRB0SqnWkl4DUx2Tt5bhn3baGf0PIujdLpT4E279zf2DWfCW-kwuaRCepNqyyHNAI6eGrQs1JeVkH4aKRpcfMzhFL1GsffwaqqEjW8HsPQxU21SuvMDnjHIAsdLDLjRwUALaYqZsQRICMiEOA";
        String sharedId = "shared_id_7108_67ac18a730e687.48545874";

        Request request = new Request.Builder()
            .url(API_URL)
            .get()
            .addHeader("shared-id", sharedId)
            .addHeader("authorization", "Bearer "+authorizationToken)
            .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36")
            .addHeader("accept", "*/*")
            .addHeader("cache-control", "no-cache")
            .addHeader("host", "123milhas.com")
            .addHeader("accept-encoding", "gzip, deflate, br")
            .addHeader("connection", "keep-alive")
            .addHeader("cookie", "WTPERSIST=; laravel_session=MIw3DXUTPGXOrG3QagzNzgEs3lIqeTkdXWcTnwiC; __cf_bm=uPrabp0g.DtHXkBXy9GMTT3UOVJW26X08Nsk46mCaAA-1739535300-1.0.1.1-7B0_qsjb3bMy4RkzBmKFYrhgAJfLE5foIj1DHyQ5xBzlXCYo0W6BdIJsA3B_UFrZ0MwB5RlKI4jjsbD_sGKKLg; AWSALB=4obCKAwqcmAH4hQiuL4wmq9adKc7Kw+yQNoLGUPS0QwXq/h66wfIiHIkh0ZId56+0Nx2i9opEX/lo3mcDmkWXgKhZHxqpRW3Yy7heoo1lSwnvx2W0hN8Tb/mlRbB; AWSALBCORS=4obCKAwqcmAH4hQiuL4wmq9adKc7Kw+yQNoLGUPS0QwXq/h66wfIiHIkh0ZId56+0Nx2i9opEX/lo3mcDmkWXgKhZHxqpRW3Yy7heoo1lSwnvx2W0hN8Tb/mlRbB")
            .build();

        boolean success = false;
        int attempt = 0;
        long waitTime = 1_000; // 1 segundo inicial
        long[] retryDelays = {100, 200, 400, 800};

        while (!success && attempt < MAX_RETRIES) {
            try (okhttp3.Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody;
                    if ("gzip".equalsIgnoreCase(response.header("Content-Encoding"))) {
                        try (GZIPInputStream gzip = new GZIPInputStream(response.body().byteStream());
                             BufferedReader br = new BufferedReader(new InputStreamReader(gzip))) {
                            responseBody = br.lines().collect(Collectors.joining());
                        }
                    } else {
                        responseBody = response.body().string();
                    }
                    // 🔹 Processa os dados aqui antes de continuar
                    System.out.println("✅ Consulta finalizada para " + arrivalAirport + " no dia " + departureDate);

                    RootData rootData = objectMapper.readValue(responseBody, RootData.class);
                    flights.addAll(rootData.getData().toEntityList());

                    Search search = SearchDTO.toEntity(rootData.getData());
                    searchRepository.save(search);
                    System.out.println("====================***************************DADOS PROCESSADOS E PERSISTIDOS COM SUCESSO!!!!!!!!!!!!!!!!!!!!!!!!!");
                    success = true;

                } else if (response.code() == 429) { // 🛑 Rate Limit
                    String retryAfter = response.header("Retry-After");
                    System.out.println("⚠️ RETRY AFTER " + retryAfter);
                    if (retryAfter != null) {
                        waitTime = Long.parseLong(retryAfter) * 1_000; // Convertendo segundos para milissegundos
                    } else {
                        waitTime *= 1; // Aumenta exponencialmente caso o header não esteja presente
                    }
                    System.out.println("⚠️ Erro 429 - Aguardando " + (waitTime / 1_000) + " segundos antes de tentar novamente...");
                } else if (response.code() == 401) {
                    System.out.println("❌ Erro na requisição: " + response.code() + " - " + response.message());
                    String limit = response.header("X-RateLimit-Limit");
                    String remaining = response.header("X-RateLimit-Remaining");
                    System.out.println("⚠️ RateLimit LIMIT " + limit);
                    System.out.println("⚠️ RateLimit REMAINING " + remaining);
                } else {
                    System.out.println("❌ Erro na requisição: " + response.code() + " - " + response.message());
                    return; // Encerra a tentativa e evita loop infinito caso seja um erro definitivo
                }
            } catch (IOException e) {
                System.out.println("❌ Erro de conexão: " + e.getMessage());
            }

            attempt++;
            try {
                Thread.sleep(waitTime); // Aguarda antes de tentar novamente
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public ResponseDTO searchFlightsExterna(String departureAirport, String arrivalAirport, String departureDate,
                              Integer adults, Integer children, Integer babies,
                              Integer classService, Integer isLoyalty) {
        String API_URL = String.format(API_URL_TEMPLATE, departureAirport, arrivalAirport, departureDate, adults,
                children, babies, classService, isLoyalty);

        String authorizationToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjNiNDE3Y2UzZGNiZWUyNThiYzUwYzNjZjJiNGE1NTE3MTAxMGUxY2UyODJkMjNlZDRkOTIwNmY0MjU2YmRjNzg2YjdkNzA0MWU2ODBmNmNkIn0.eyJhdWQiOiIzIiwianRpIjoiM2I0MTdjZTNkY2JlZTI1OGJjNTBjM2NmMmI0YTU1MTcxMDEwZTFjZTI4MmQyM2VkNGQ5MjA2ZjQyNTZiZGM3ODZiN2Q3MDQxZTY4MGY2Y2QiLCJpYXQiOjE3NDAwMTUzMzMsIm5iZiI6MTc0MDAxNTMzMywiZXhwIjoxNzQwMTAxNzMzLCJzdWIiOiIiLCJzY29wZXMiOltdfQ.HMBa7iFSU_kuRi4PFsi0A9U_eDR5GkF3AyKA6FS5ck2Ua1tUw2SCSXYxYUQ7gnyHe42MBhIW4MRxG3HJTEkP2DqEot4JOmODsg8lUHLjXbzz7V5a_1j1ra31n5aMhkFwjQ7CH-paGy-UpRBoIRSWp33qrheXsayecC2GAqUpDFdaKpqGxB3wLqj1JaSouLp_iun-VUB2HNJSI0TDdACf0njMWLiy1DjU7N8FjXhQ3bdPlBvap82c7ZqS1R1_xndJZGIZMGh3kOIqRHTL6uyr_CAi1RFYr9JX7nD2A5fUreymCGZF5ddb0bX5Zc1S1J6iEpVhti3U2q_xvtFvxVI527BUq6C7ED5o61aAnYKwPnPqZhlu0iJQ9Zo4BNNev9iptNFbnFIOoQHR-Hbp6JwkahztlhLqN3gCJ5ovvWrruZJf_IdXfj1OhlPdbM5ZVdcPyq3A9arei8nAG_BIJ6Tuzhqk7j4VvMFQFhjYszpouRNp23uONpOaNytb3TrOCnhOMASpR7nx5g1pvRkCqwfyCnqbk5DEs3HuULoRuGz6QPHIufgvOhQnsCleEjMYImOGnCNymzzuHpn8PIg3l0AKlyJs_-H1X-XWV6G9DYpvfp6AGa9oG0CMFGDLEisqewRoHaHRjrJ_DXIz5JtpioUIWZLF8YPP7htgRQ-eb5whyH4";
        String sharedId = "shared_id_7108_67ac18a730e687.48545874";
        ResponseDTO responseDTO = new ResponseDTO();
        Request request = new Request.Builder()
            .url(API_URL)
            .get()
            .addHeader("shared-id", sharedId)
            .addHeader("authorization", "Bearer "+authorizationToken)
            .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36")
            .addHeader("accept", "*/*")
            .addHeader("cache-control", "no-cache")
            .addHeader("host", "123milhas.com")
            .addHeader("accept-encoding", "gzip, deflate, br")
            .addHeader("connection", "keep-alive")
            .addHeader("cookie", "WTPERSIST=; laravel_session=MIw3DXUTPGXOrG3QagzNzgEs3lIqeTkdXWcTnwiC; __cf_bm=uPrabp0g.DtHXkBXy9GMTT3UOVJW26X08Nsk46mCaAA-1739535300-1.0.1.1-7B0_qsjb3bMy4RkzBmKFYrhgAJfLE5foIj1DHyQ5xBzlXCYo0W6BdIJsA3B_UFrZ0MwB5RlKI4jjsbD_sGKKLg; AWSALB=4obCKAwqcmAH4hQiuL4wmq9adKc7Kw+yQNoLGUPS0QwXq/h66wfIiHIkh0ZId56+0Nx2i9opEX/lo3mcDmkWXgKhZHxqpRW3Yy7heoo1lSwnvx2W0hN8Tb/mlRbB; AWSALBCORS=4obCKAwqcmAH4hQiuL4wmq9adKc7Kw+yQNoLGUPS0QwXq/h66wfIiHIkh0ZId56+0Nx2i9opEX/lo3mcDmkWXgKhZHxqpRW3Yy7heoo1lSwnvx2W0hN8Tb/mlRbB")
            .build();

        boolean success = false;
        int attempt = 0;
        long waitTime = 1_000;

        while (!success && attempt < MAX_RETRIES) {
            try (okhttp3.Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody;
                    if ("gzip".equalsIgnoreCase(response.header("Content-Encoding"))) {
                        try (GZIPInputStream gzip = new GZIPInputStream(response.body().byteStream());
                             BufferedReader br = new BufferedReader(new InputStreamReader(gzip))) {
                            responseBody = br.lines().collect(Collectors.joining());
                        }
                    } else {
                        responseBody = response.body().string();
                    }
                    // 🔹 Processa os dados aqui antes de continuar
                    System.out.println("✅ Consulta finalizada para " + arrivalAirport + " no dia " + departureDate);

                    RootData rootData = objectMapper.readValue(responseBody, RootData.class);
                    flights.addAll(rootData.getData().toEntityList());

                    Search search = SearchDTO.toEntity(rootData.getData());
                    //searchRepository.save(search);

                    List<FlightOneTwoThree> flights = search.getFlights();

                    List<FlightOneTwoThreeDTO> flightsResponse = flights.stream()
                        .map(flightOneTwoThreeConverter::convertToDTO)
                        .collect(Collectors.toList());

                    FlightOneTwoThreeResponseDTO responseExterna = new FlightOneTwoThreeResponseDTO();
                    responseExterna.setFlights(flightsResponse.stream().map(flightOneTwoThreeConverter::convertToDTOResponse).collect(Collectors.toList()));
                    responseDTO.setFlights(responseExterna.getFlights());

                    System.out.println("====================***************************DADOS PROCESSADOS E PERSISTIDOS COM SUCESSO!!!!!!!!!!!!!!!!!!!!!!!!!");
                    success = true;

                } else if (response.code() == 429) { // 🛑 Rate Limit
                    String retryAfter = response.header("Retry-After");
                    System.out.println("⚠️ RETRY AFTER " + retryAfter);
                    if (retryAfter != null) {
                        waitTime = Long.parseLong(retryAfter) * 1_000; // Convertendo segundos para milissegundos
                    } else {
                        waitTime *= 1; // Aumenta exponencialmente caso o header não esteja presente
                    }
                    System.out.println("⚠️ Erro 429 - Aguardando " + (waitTime / 1_000) + " segundos antes de tentar novamente...");
                } else if (response.code() == 401) {
                    System.out.println("❌ Erro na requisição: " + response.code() + " - " + response.message());
                    String limit = response.header("X-RateLimit-Limit");
                    String remaining = response.header("X-RateLimit-Remaining");
                    System.out.println("⚠️ RateLimit LIMIT " + limit);
                    System.out.println("⚠️ RateLimit REMAINING " + remaining);
                } else {
                    System.out.println("❌ Erro na requisição: " + response.code() + " - " + response.message());
                }
            } catch (IOException e) {
                System.out.println("❌ Erro de conexão: " + e.getMessage());
            }

            attempt++;
            try {
                Thread.sleep(waitTime); // Aguarda antes de tentar novamente
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return responseDTO;
    }
}
