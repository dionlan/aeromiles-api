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
import java.util.Optional;

@SpringBootApplication
@ComponentScan(basePackages = "com.aeromiles")
public class TesteMainTeste {

    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\dius_\\Downloads\\selenium 132\\132.0.6834.83\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(options);

        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.addListener(Network.responseReceived(), response -> {
            RequestId requestId = response.getRequestId();
            if (response.getResponse().getUrl().contains("https://123milhas.com/api/v3/flight/search")) {
                System.out.println("Requisição para URL: " + response.getResponse().getUrl());
                System.out.println("Status Code: " + response.getResponse().getStatus());
                // Captura o conteúdo JSON da resposta
                String res = devTools.send(Network.getResponseBody(requestId)).getBody();
                System.out.println("Resposta: " + res);
            }
        });

        try {
            // Acessa a URL desejada
            String targetUrl = "https://123milhas.com/v2/busca?de=BSB&para=GRU&ida=06-02-2025&adultos=1&criancas=0&bebes=0&classe=3&is_loyalty=0";
            driver.get(targetUrl);

            Thread.sleep(30000); // Espera 30 segundos para garantir que o carregamento seja concluído

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Fecha o navegador
            driver.quit();
        }
    }
}

