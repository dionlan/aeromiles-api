package com.aeromiles.webscraping;

import com.aeromiles.service.OneTwoThreeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.aeromiles")
public class Scrapping123Milhas {

    public static void main(String[] args) {
        // Iniciar o contexto Spring Boot
        ConfigurableApplicationContext context = SpringApplication.run(Scrapping123Milhas.class, args);

        // Obter o bean do serviço OneTwoThreeService
        OneTwoThreeService oneTwoThreeService = context.getBean(OneTwoThreeService.class);

        // Executar o método save()
        oneTwoThreeService.save();
    }
}
