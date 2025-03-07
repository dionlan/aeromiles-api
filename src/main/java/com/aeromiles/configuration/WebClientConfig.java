package com.aeromiles.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/*
    O RestTemplate está em desuso para novas aplicações. O WebClient, da biblioteca Spring WebFlux, é mais moderno,
    baseado em reatividade, e preparado para cenários assíncronos e de alta concorrência.
 */
@Configuration
public class WebClientConfig {

    private static final HttpHeaders HEADERS;

    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

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

    @Bean
    public WebClient webClient() {

        return WebClient.builder()
            .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36")
            .baseUrl("https://bff-mall.maxmilhas.com.br")
            .codecs(configurer -> {
                // Aumentando o limite do buffer de dados (NECESSÁRIO PARA A LATAM)
                configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024); // 10 MB
            })/*.filter(logRequest()) // Log de requisição
            .filter(logResponse())*/ // Log de resposta
            .build();
        /*WebClient.Builder webClientBuilder = builder.baseUrl("https://bff-mall.maxmilhas.com.br")
        .codecs(configurer -> {
            // Aumentando o limite do buffer de dados (NECESSÁRIO PARA A LATAM)
            configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024); // 10 MB
        });

        HEADERS.forEach((key, values) -> values.forEach(value -> webClientBuilder.defaultHeader(key, value))
        );

        return webClientBuilder.build(); */
    }

    /*
        return webClient
        .get()
        .uri(url)
        .headers(headers -> headers.addAll(HEADERS)) // Configurando os cabeçalhos
        .retrieve()
        .bodyToMono(String.class)
        .map(this::extractJsonFromHtml) // Usando o método com Jsoup
        .block(); // Bloqueando para obter a resposta síncrona
     */

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            logger.info("Request: [{} {}]", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) ->
                    values.forEach(value -> logger.info("Header '{}': {}", name, value))
            );
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            logger.info("Response Status Code: {}", clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders().forEach((name, values) ->
                    values.forEach(value -> logger.info("Response Header '{}': {}", name, value))
            );
            return Mono.just(clientResponse);
        });
    }
}
