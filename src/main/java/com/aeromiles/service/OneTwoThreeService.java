package com.aeromiles.service;

import com.aeromiles.model.onetwothree.Search;
import com.aeromiles.model.onetwothree.dto.RootData;
import com.aeromiles.model.onetwothree.dto.SearchDTO;
import com.aeromiles.repository.SearchRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v131.network.Network;
import org.openqa.selenium.devtools.v131.network.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OneTwoThreeService {

    @Autowired
    private SearchRepository searchRepository;

    public void save() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Configuração do ChromeDriver
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\dius_\\Downloads\\chromedriver131\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        // Iniciar sessão no DevTools
        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        // Habilitar monitoramento de rede
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // Endpoint alvo para monitoramento
        final String targetUrl = "https://123milhas.com/api/v3/flight/search";

        // Listener para capturar a resposta do endpoint específico
        devTools.addListener(Network.responseReceived(), response -> {
            Response res = response.getResponse();
            String requestUrl = res.getUrl();

            // Verifica se a URL da resposta é o endpoint que queremos monitorar
            if (requestUrl.contains(targetUrl)) {
                System.out.println("Interceptando resposta da URL: " + requestUrl);

                Optional<Network.GetResponseBodyResponse> responseBody =
                        Optional.of(devTools.send(Network.getResponseBody(response.getRequestId())));

                if(res.getStatus().equals(200)){
                    responseBody.ifPresent(body -> {
                        String responseContent = body.getBody();
                        System.out.println("Resposta JSON do endpoint:");
                        System.out.println(responseContent);

                        try {
                            RootData searchDTO = objectMapper.readValue(responseContent, RootData.class);
                            Search search = SearchDTO.toEntity(searchDTO.getData());
                            // Associar cada FlightOneTwoThree à entidade Search
                            if (search.getFlights() != null) {
                                search.getFlights().forEach((key, flight) -> flight.setSearch(search));
                            }
                            // Salvar entidade Search (JPA persistirá os Flights automaticamente)
                            searchRepository.save(search);

                            System.out.println("Dados persistidos com sucesso!");
                        } catch (Exception e) {
                            System.err.println("Erro ao processar e salvar resposta: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }
            }
        });

        try {
            // Acessar o site inicial
            driver.get("https://123milhas.com/v2/busca?de=BSB&para=CGH&ida=29-01-2025&volta=04-02-2025&adultos=1&criancas=0&bebes=0&classe=3&is_loyalty=1");

            // Aguarde o tempo necessário para que o endpoint seja acessado
            Thread.sleep(10000); // Ajuste conforme o tempo médio de carregamento do site
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Fechar o navegador e o contexto do Spring
            driver.quit();
        }
    }
}
