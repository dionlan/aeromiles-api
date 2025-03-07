package com.aeromiles.webscraping;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v131.network.Network;

import java.util.Arrays;
import java.util.Optional;

public class ChromeConfiguration {

    public static WebDriver configureChromeForMinimalResources() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Roda sem interface gráfica
        options.addArguments("--disable-gpu"); // Desativa o uso de GPU
        options.addArguments("--no-sandbox"); // Necessário em alguns ambientes
        options.addArguments("--disable-dev-shm-usage"); // Evita problemas em sistemas com pouca memória compartilhada
        options.addArguments("--disable-images"); // Bloqueia imagens (melhora desempenho em modos antigos)
        options.addArguments("--disable-extensions"); // Desativa extensões do Chrome
        options.addArguments("--window-size=1920,1080"); // Define tamanho padrão para a janela virtual
        options.addArguments("--remote-allow-origins=*"); // Evita erros com algumas versões do WebDriver

        // Inicializa o WebDriver
        ChromeDriver driver = new ChromeDriver();
        DevTools devTools = driver.getDevTools();

        // Cria uma sessão DevTools
        devTools.createSession();

        // Ativa a interceptação de rede
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // Bloqueia tipos de recursos desnecessários
        devTools.send(Network.setBlockedURLs(Arrays.asList(
            "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", // Imagens
            "*.css", "*.woff", "*.woff2", "*.ttf", "*.svg", // Estilos e fontes
            "*.mp4", "*.avi", "*.mov", "*.mkv", "*.flv", "*.webm", // Vídeos
            "*.mp3", "*.wav", "*.ogg" // Áudio
        )));

        // Opcional: Desativa cache para garantir que somente os recursos essenciais sejam carregados
        devTools.send(Network.setCacheDisabled(true));

        System.out.println("Configuração do Chrome para uso otimizado concluída.");
        return driver;
    }
}
