package com.aeromiles;

import com.aeromiles.service.OneTwoThreeService;
import com.aeromiles.webscraping.TesteMain;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v132.network.Network;
import org.openqa.selenium.devtools.v132.network.model.RequestId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootApplication
public class AeromilesApplication {

    private static final String CHROME_DRIVER_PATH = "C:\\Users\\dius_\\Downloads\\selenium 132\\132.0.6834.83\\chromedriver-win64\\chromedriver.exe";
    private static final String TARGET_URL = "https://123milhas.com/api/v3/flight/search";

    public static void main(String[] args) {
        SpringApplication.run(AeromilesApplication.class, args);
        //start(args);
    }
    private static void start(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TesteMain.class, args);
        OneTwoThreeService oneTwoThreeService = context.getBean(OneTwoThreeService.class);

        Instant startTime = Instant.now();

        String departureAirport = "BSB";
        List<String> arrivalAirports = List.of("GRU", "GIG", "CNF", "VCP", "SDU", "REC", "SSA", "POA", "FOR",
            "GRU", "GIG", "CNF", "VCP", "SDU", "REC", "SSA", "POA", "FOR", "BEL",
            "VIX", "GYN", "MAO", "CGB", "IGU", "NAT", "CGH", "MCZ", "JPA", "SLZ",
            "CWB", "AJU", "BPS", "TFF", "FLN", "IOS", "PMW", "THE", "BVB", "RBR", "PNZ"
        );

        LocalDate startDate = LocalDate.of(2025, 2, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (String arrivalAirport : arrivalAirports) {
            System.out.println("Iniciando pesquisa para o destino: " + arrivalAirport);

            AtomicInteger dayCounter = new AtomicInteger(0);
            long totalDays = startDate.datesUntil(endDate.plusDays(1)).count();

            for (LocalDate currentDate = startDate; !currentDate.isAfter(endDate); currentDate = currentDate.plusDays(1)) {
                String departureTime = currentDate.format(formatter);
                int currentProgress = dayCounter.incrementAndGet();
                System.out.println("Pesquisando voos para " + arrivalAirport + " no dia " + departureTime + " (" + currentProgress + "/" + totalDays + ")");

                String dateRange = departureTime; // Usado para identificar o intervalo no log

                // Reiniciar navegador e DevTools para cada iteração
                processDateRange(departureAirport, arrivalAirport, departureTime, 1, 0, 0, 3, 0, oneTwoThreeService);

                System.out.println("Consulta finalizada para o destino " + arrivalAirport + " no dia " + departureTime);
            }
        }

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        LocalTime executionTime = LocalTime.ofSecondOfDay(duration.getSeconds());
        System.out.println("Duração da execução: " + executionTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private static void processDateRange(String departureAirport, String arrivalAirport, String departureTime,
                                         int adults, int children, int babies, int classType, int isLoyalty, OneTwoThreeService oneTwoThreeService) {

            // Inicializar o navegador e DevTools
            /*ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-gpu", "--no-sandbox", "--window-size=600,400");
            driver = new ChromeDriver(options);
            DevTools devTools = ((ChromeDriver) driver).getDevTools();
            devTools.createSession();*/

            // Configurar DevTools e ouvintes
            /*CountDownLatch latch = new CountDownLatch(1);

            // Configurar DevTools e ouvintes
            configureDevTools(devTools, latch, departureAirport, arrivalAirport, departureTime, 1, 0, 0, 3, 0, oneTwoThreeService);
            */
            String url_template = "https://123milhas.com/v2/busca?de=%s&para=%s&ida=%s&adultos=%d&criancas=%d&bebes=%d&classe=%d&is_loyalty=%d";
            String url_base = String.format(url_template, departureAirport, arrivalAirport, departureTime, adults, children, babies, classType, isLoyalty);

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-gpu", "--no-sandbox", "--window-size=600,400");
            options.addArguments("--start-minimized"); // Inicia o Chrome minimizado

            WebDriver driver = new ChromeDriver(options);
            DevTools devTools = ((ChromeDriver) driver).getDevTools();
            devTools.createSession();

            try {
                devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
                String[] responseBody = {null};
                devTools.addListener(Network.responseReceived(), response -> {
                    String url = response.getResponse().getUrl();
                    if (url.contains(TARGET_URL) && response.getResponse().getStatus() == 200) {
                        try {
                            Thread.sleep(1000);
                            String body = devTools.send(Network.getResponseBody(response.getRequestId())).getBody();
                            responseBody[0] = body; // Armazena o corpo da resposta
                            persistResponse(responseBody[0], oneTwoThreeService);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                driver.get(url_base);
                Thread.sleep(20000);

                if (responseBody[0] != null) {
                    System.out.println("Resposta capturada com sucesso!");
                } else {
                    System.out.println("Nenhuma resposta capturada para o endpoint.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                driver.quit();
            }
    }

    private static String fetchResponseFromUrl(String targetUrl) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // Verifica o código de resposta
            if (connection.getResponseCode() == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
            } else {
                System.err.println("Erro: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    private static void configureDevTools(DevTools devTools, CountDownLatch latch, String departureAirport, String arrivalAirport, String departureTime,
                                          int adults, int children, int babies, int classType, int isLoyalty, OneTwoThreeService oneTwoThreeService) {
        AtomicReference<RequestId> requestId = new AtomicReference<>();
        AtomicBoolean loginSso = new AtomicBoolean(false);

        String LOGIN_SSO_ENDPOINT = "https://123milhas.com/api/v3/client";

        // Listener para capturar a resposta do target URL
        devTools.addListener(Network.responseReceived(), response -> {

            System.out.println("URL: " + response.getResponse().getUrl());

            String urlSoo = response.getResponse().getUrl();
            if (urlSoo.contains(LOGIN_SSO_ENDPOINT)) {
                System.out.println("Autenticação loginSso concluída.");
                loginSso.set(true);
            }

            String url = response.getResponse().getUrl();
            if (url.contains(TARGET_URL) && response.getResponse().getStatus() == 200) {
                System.out.println("Resposta recebida para a url: " + url);
                requestId.set(response.getRequestId());
                try {
                    Network.GetResponseBodyResponse responseBody =
                            devTools.send(Network.getResponseBody(requestId.get()));
                    String resposta = responseBody.getBody();
                    System.out.println("Resposta capturada: " + resposta);
                    // Persistir no banco ou processar os dados
                    persistResponse(resposta, oneTwoThreeService);
                    latch.countDown();
                } catch (Exception e) {
                    System.err.println("Erro no listener: " + e.getMessage());
                }

            }
        });

        // Ativar o Network DevTools
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    }

    private static void persistResponse(String response, OneTwoThreeService oneTwoThreeService) {
        // Lógica para persistir os dados no banco de dados
        oneTwoThreeService.processResponse(response);
    }

	/*public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(AeromilesApplication.class, args);
		FlightService flightService = context.getBean(FlightService.class);
		long startTime = System.currentTimeMillis(); // Inicia a contagem do tempo

		String departureAirport = "BSB";
		List<String> arrivalAirports = List.of(
				"GRU", // São Paulo - Guarulhos
				"GIG", // Rio de Janeiro - Galeão
				"CNF", // Belo Horizonte - Confins
				"VCP", // Campinas - Viracopos
				"SDU", // Rio de Janeiro - Santos Dumont
				"REC", // Recife
				"SSA", // Salvador
				"POA", // Porto Alegre
				"FOR", // Fortaleza
				"BEL", // Belém
				"VIX", // Vitória
				"GYN", // Goiânia
				"MAO", // Manaus
				"CGB", // Cuiabá
				"IGU", // Foz do Iguaçu
				"NAT", // Natal
				"CGH", // São Paulo - Congonhas
				"MCZ", // Maceió
				"JPA", // João Pessoa
				"SLZ", // São Luís
				"CWB", // Curitiba
				"AJU", // Aracaju
				"BPS", // Porto Seguro
				"TFF", // Tefé
				"FLN", // Florianópolis
				"IOS", // Ilhéus
				"PMW", // Palmas
				"THE", // Teresina
				"BVB", // Boa Vista
				"RBR", // Rio Branco
				"PNZ"  // Petrolina
		);*/
        /*List<String> arrivalAirports = List.of(
                "LIS", // Lisboa - Portugal
                "MIA", // Miami - EUA
                "EZE", // Buenos Aires - Argentina
                "CDG", // Paris - França
                "FRA", // Frankfurt - Alemanha
                "SCL", // Santiago - Chile
                "MAD", // Madrid - Espanha
                "YYZ", // Toronto - Canadá
                "JFK", // Nova York - EUA
                "MCO"  // Orlando - EUA
        );*/
		/*int passengers = 1;

		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 12, 31);

		int threadPoolSize = 10; // Número de threads para paralelismo
		ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

		Map<String, List<Flight>> cheapestFlightsByAirport = new HashMap<>();

		for (String arrivalAirport : arrivalAirports) {
			System.out.println("Iniciando pesquisa para o destino: " + arrivalAirport);

			List<CompletableFuture<List<Flight>>> futures = new ArrayList<>();
			AtomicInteger dayCounter = new AtomicInteger(0);
			long totalDays = startDate.datesUntil(endDate.plusDays(1)).count();

			for (LocalDate currentDate = startDate; !currentDate.isAfter(endDate); currentDate = currentDate.plusDays(1)) {
				String departureTime = currentDate.toString();
				futures.add(CompletableFuture.supplyAsync(() -> {
					int currentProgress = dayCounter.incrementAndGet();
					System.out.println("Pesquisando voos para " + arrivalAirport + " no dia " + departureTime + " (" + currentProgress + "/" + totalDays + ")");
					try {
						List<Flight> flights = flightService.searchFlights(departureAirport, arrivalAirport, departureTime, passengers);

						// Exibe o menor valor encontrado no dia atual
						flights.stream()
							.filter(flight -> flight.getMiles() > 0)
							.min(Comparator.comparingInt(Flight::getMiles))
							.ifPresent(flight ->
									System.out.println("Voo encontrado para " + arrivalAirport + " no dia " + departureTime + ": " + flight.getMiles() + " milhas.")
							);

						return flights;
					} catch (Exception e) {
						System.err.println("Erro ao buscar voos para " + arrivalAirport + " no dia " + departureTime + ": " + e.getMessage());
						return Collections.emptyList();
					}
				}, executor));
			}

			// Aguarda todas as pesquisas e coleta os resultados
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
			List<Flight> allFlights = futures.stream()
				.map(CompletableFuture::join)
				.flatMap(List::stream)
				.filter(flight -> flight.getMiles() > 0)
				.sorted(Comparator.comparingInt(Flight::getMiles)) // Ordena por milhas
				.limit(5) // Mantém os 5 voos mais baratos
				.toList();

			// Adiciona ao mapa de resultados
			cheapestFlightsByAirport.put(arrivalAirport, allFlights);
		}

		// Exibe os 5 voos mais baratos para cada destino
		System.out.println("Resumo dos 5 voos mais baratos em milhas SMILES por destino:");
		for (Map.Entry<String, List<Flight>> entry : cheapestFlightsByAirport.entrySet()) {
			String arrivalAirport = entry.getKey();
			List<Flight> flights = entry.getValue();

			System.out.println("Destino: " + arrivalAirport);
			if (flights.isEmpty()) {
				System.out.println("  Nenhum voo encontrado.");
			} else {
				for (Flight flight : flights) {
					System.out.println("  Voo: " + flight);
				}
			}
		}

		// Finaliza o executor
		executor.shutdown();

		long endTime = System.currentTimeMillis(); // Finaliza a contagem do tempo
		long elapsedTimeMinutes = (endTime - startTime) / (1000 * 60); // Converte para minutos
		System.out.println("Tempo total de execução: " + elapsedTimeMinutes + " minutos.");

		System.exit(0); // Encerra a execução
	}*/
}
