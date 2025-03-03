package com.aeromiles.configuration;

import okhttp3.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RetryInterceptor implements Interceptor {

    private static final List<String> USER_AGENTS = List.of(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Safari/605.1.15",
        "Mozilla/5.0 (Linux; Android 10; SM-G960U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
    );

    private static final int MAX_RETRIES = 3;
    private final AtomicInteger retryCount = new AtomicInteger(0);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Response response = chain.proceed(originalRequest);

        // Verifica se o status é 429 e se ainda há tentativas restantes
        while (response.code() == 429 && retryCount.get() < MAX_RETRIES) {
            retryCount.incrementAndGet();
            System.out.println("⚠️ Recebido status 429. Tentativa " + retryCount.get() + " de " + MAX_RETRIES);

            // Fecha a resposta anterior para liberar recursos
            response.close();

            // Cria uma nova requisição com headers modificados
            Request newRequest = originalRequest.newBuilder()
                .header("User-Agent", USER_AGENTS.get(retryCount.get() % USER_AGENTS.size())) // Rotaciona User-Agent
                .build();

            // Realiza a nova requisição
            response = chain.proceed(newRequest);
        }

        return response;
    }
}
