package com.aeromiles.configuration;

import okhttp3.OkHttpClient;
import okhttp3.ConnectionPool;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class HttpClientConfig {

    public static OkHttpClient createClient() {
        //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("177.73.68.150", 8080));
        return new OkHttpClient.Builder()
            //.proxy(proxy)
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(30))
            .writeTimeout(Duration.ofSeconds(30))
            .connectionPool(new ConnectionPool(10, 10, TimeUnit.MINUTES))
            .build();
    }
}
