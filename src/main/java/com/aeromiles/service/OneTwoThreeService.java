package com.aeromiles.service;

import com.aeromiles.model.onetwothree.FlightOneTwoThree;
import com.aeromiles.model.onetwothree.Search;
import com.aeromiles.model.onetwothree.dto.RootData;
import com.aeromiles.model.onetwothree.dto.SearchDTO;
import com.aeromiles.repository.SearchRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class OneTwoThreeService {

    private static final Logger logger = LoggerFactory.getLogger(OneTwoThreeService.class);

    @Autowired
    private SearchRepository searchRepository;
    private static final String API_URL_TEMPLATE = "https://123milhas.com/v2/busca?de=%s&para=%s&ida=%s&adultos=%d&criancas=%d&bebes=%d&classe=%d&is_loyalty=%d";
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<FlightOneTwoThree> flights = new ArrayList<>();

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

    private void processResponse(String responseContent) {
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
}
