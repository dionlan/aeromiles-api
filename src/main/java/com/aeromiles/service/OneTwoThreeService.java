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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
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
        //options.addArguments("--headless=new");
        options.addArguments("--disable-gpu"); // Desabilita a GPU (opcional)
        options.addArguments("--no-sandbox"); // Para evitar problemas de segurança em ambientes Linux
        options.addArguments("--window-size=600,400"); // Define o tamanho da janela (opcional)
        options.addArguments("disable-extensions"); // Desabilita extensõe
        options.addArguments("--disable-background-timer-throttling", "--disable-backgrounding-occluded-windows");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(15));

        try {
            DevTools devTools = ((ChromeDriver) driver).getDevTools();
            setupDevTools(devTools);

            final int MAX_RETRIES = 3;
            boolean pageLoaded = false;

            for (int attempts = 0; attempts < MAX_RETRIES; attempts++) {
                driver.get(url);

                // Verifica se a página carregou completamente
                if (PageLoadHelper.isPageLoaded(driver)) {
                    System.out.println("Página carregada completamente.");
                    pageLoaded = true;
                    break;
                } else {
                    System.err.println("Falha ao carregar a página. Tentativa " + (attempts + 1));
                }
            }

            if (!pageLoaded) {
                System.err.println("Falha ao carregar a página após " + MAX_RETRIES + " tentativas.");
                return flights;
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.flights")));

            /*WebElement flightsDiv = driver.findElement(By.cssSelector("div.flights"));
            //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            //wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.flights")));
            if (!PageLoadHelper.waitForElement(driver, flightsDiv)) {
                System.err.println("Elemento esperado não encontrado. Abortando execução.");
                return flights;
            }*/

            final String targetUrl = "https://123milhas.com/api/v3/flight/search";
            CompletableFuture<Void> jsonReadyFuture = new CompletableFuture<>();
            AtomicBoolean jsonProcessed = new AtomicBoolean(false); // Sinaliza o processamento concluído

            devTools.addListener(Network.responseReceived(), response ->
                handleResponse(response, targetUrl, devTools, jsonReadyFuture, jsonProcessed)
            );

            waitForJsonProcessing(jsonReadyFuture); // Espera a resposta ser processada
            System.out.println("JSON tratado e salvo no banco com sucesso.");

        } catch (Exception e) {
            System.err.println("Erro ao executar o método save: " + e.getMessage());
        } finally {
            driver.quit();
        }
        return flights;
    }

    private void setupDevTools(DevTools devTools) {
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.send(Network.setBlockedURLs(Arrays.asList(
            "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", // Imagens
            "*.css", "*.woff", "*.woff2", "*.ttf", "*.svg", // Estilos e fontes
            "*.mp4", "*.avi", "*.mov", "*.mkv", "*.flv", "*.webm", // Vídeos
            "*.mp3", "*.wav", "*.ogg" // Áudio
        )));

        devTools.send(Network.setCacheDisabled(true));
    }

    private void handleResponse(ResponseReceived response, String targetUrl, DevTools devTools, CompletableFuture<Void> jsonReadyFuture, AtomicBoolean jsonProcessed) {
        Response res = response.getResponse();

        if (res.getUrl().contains(targetUrl) && res.getStatus() == 200) {
            System.out.println("Interceptando resposta do endpoint: " + res.getUrl());

            try {
                Thread.sleep(500);
                Network.GetResponseBodyResponse responseBody = devTools.send(Network.getResponseBody(response.getRequestId()));
                String json = responseBody.getBody();

                processResponse(json);
                Thread.sleep(500);
                jsonProcessed.set(true);
                jsonReadyFuture.complete(null);

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
            Thread.sleep(500);
            flights.addAll(rootData.getData().toEntityList());

            Search search = SearchDTO.toEntity(rootData.getData());
            searchRepository.save(search);
            Thread.sleep(1000);
            System.out.println("Dados processados e persistidos com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao processar resposta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
