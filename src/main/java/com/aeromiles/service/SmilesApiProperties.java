package com.aeromiles.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "smiles.api")
public class SmilesApiProperties {
    private String baseUrl = "https://api-air-flightsearch-green.smiles.com.br/v1/airlines/search";
    private String apiKey = "aJqPU7xNHl9qN3NVZnPaJ208aPo2Bh2p2ZV844tw";
    private boolean useProxy = false;
    private ProxySettings proxy;

    @Data
    public static class ProxySettings {
        private String host;
        private int port;
        private String username;
        private String password;
    }
}
