package com.aeromiles.webscraping;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v132.network.Network;
import org.openqa.selenium.devtools.v132.network.model.RequestId;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
@ComponentScan(basePackages = "com.aeromiles")
public class TesteMain {

    public static void main(String[] args) {
        Instant startTime = Instant.now();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu", "--no-sandbox", "--window-size=600,400");
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\dius_\\Downloads\\selenium 132\\132.0.6834.83\\chromedriver-win64\\chromedriver.exe");

        WebDriver driver = new ChromeDriver(options);
        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        CountDownLatch latch = new CountDownLatch(1);

        AtomicBoolean loginSso = new AtomicBoolean(false);

        // Listener para rastrear chamadas de autenticação
        String loginSsoEndpoint = "https://123milhas.com/api/v3/home/search-wait"; // Exemplo de URL de autenticação
        devTools.addListener(Network.responseReceived(), response -> {
            String url = response.getResponse().getUrl();
            if (url.contains(loginSsoEndpoint)) {
                System.out.println("Autenticação loginSso concluída.");
                loginSso.set(true);
            }
        });

        // Listener para capturar/rastrear a resposta do endpoint desejado
        String targetUrl = "https://123milhas.com/api/v3/flight/search";
        devTools.addListener(Network.responseReceived(), response -> {
            if (loginSso.get()) {
                String url = response.getResponse().getUrl();
                if (url.contains(targetUrl)) {
                    System.out.println("Resposta recebida do URL: " + url);
                    try {
                        RequestId requestId = response.getRequestId();
                        Network.GetResponseBodyResponse responseBody = devTools.send(Network.getResponseBody(requestId));
                        System.out.println("Resposta capturada:");
                        System.out.println(responseBody.getBody());
                        latch.countDown();
                    } catch (Exception e) {
                        System.err.println("Erro no listener: " + e.getMessage());
                    }
                }
            }
        });

        try {
            driver.get("https://123milhas.com/v2/busca?de=BSB&para=GRU&ida=06-02-2025&adultos=1&criancas=0&bebes=0&classe=3&is_loyalty=0");
            System.out.println("Aguardando resposta da API...");
            latch.await();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            driver.quit();
            System.out.println("Navegador fechado.");

            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);

            LocalTime executionTime = LocalTime.ofSecondOfDay(duration.getSeconds());
            System.out.println("Duração da execução: " + executionTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    }
}

