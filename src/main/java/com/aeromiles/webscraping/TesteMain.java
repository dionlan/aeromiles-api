package com.aeromiles.webscraping;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v132.network.Network;
import org.openqa.selenium.devtools.v132.network.model.RequestId;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
@ComponentScan(basePackages = "com.aeromiles")
public class TesteMain {

    public static void main(String[] args) {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu", "--no-sandbox", "--window-size=600,400");
        options.addArguments("--disable-background-timer-throttling", "--disable-backgrounding-occluded-windows");
        //options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\dius_\\Downloads\\selenium 132\\132.0.6834.83\\chromedriver-win64\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // Sincronização para aguardar a captura da resposta
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

        /*AtomicBoolean login = new AtomicBoolean(false);

        // Listener para rastrear chamadas de autenticação
        String loginEndpoint = "https://auth.123milhas.com/oauth/authorize"; // Exemplo de URL de autenticação
        devTools.addListener(Network.responseReceived(), response -> {
            String url = response.getResponse().getUrl();
            if (url.contains(loginEndpoint)) {
                System.out.println("Autenticação concluída.");
                login.set(true);
            }
        });*/

        // Listener para capturar a resposta do endpoint desejado
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
                    } catch (Exception e) {
                        System.err.println("Erro ao capturar o corpo da resposta: " + e.getMessage());
                    }
                }
            }
        });

        try {
            // Navegar para a URL desejada
            driver.get("https://123milhas.com/v2/busca?de=BSB&para=GRU&ida=06-02-2025&adultos=1&criancas=0&bebes=0&classe=3&is_loyalty=0");

            // Configurar o WebDriverWait para aguardar o carregamento inicial
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
            //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.group-selector--to-new-search")));
            //wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.searchResultGroup.search-result__container.search-result.group.search-result__flights-container")));
            /*wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.loading-search-container")));*/
            System.out.println("Aguardando resposta da API...");

            // Aguarda até que o listener capture a resposta
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Fechar o driver após a execução
            driver.quit();
        }
    }
}

