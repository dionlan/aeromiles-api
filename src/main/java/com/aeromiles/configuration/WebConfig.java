package com.aeromiles.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:4200",
                "https://simovel-app.vercel.app",
                "https://fearless-peace-production.up.railway.app"
            )
            .allowCredentials(true)
            .allowedHeaders("*")
            .allowedMethods("POST", "GET", "DELETE", "PUT", "PATCH", "OPTIONS");
    }
}
