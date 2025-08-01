package com.aeromiles;

import com.aeromiles.service.FlightSearchService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class AeromilesApplicationMaxMilhasApi {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AeromilesApplicationMaxMilhasApi.class, args);
        FlightSearchService flightSearchService = context.getBean(FlightSearchService.class);

        Instant startTime = Instant.now();
        ExecutorService executorService = Executors.newFixedThreadPool(10); // 🔹 Para rodar requisições em paralelo

        String departureAirport = "BSB";
        List<String> arrivalAirports = List.of("GDR");
                /*List.of("MIA", "MCO", "SCL", "EZE"*//*, "SDU", "GIG", "MCZ", "JPA", "FOR", "NAT", "MIA", "MCO", "EZE", "SCL", "CDG", "YYZ" "FLN", "SDU", "GIG", "GRU", "CNF", "VCP", "REC", "SSA", "POA", "FOR",
                "BEL", "VIX", "GYN", "MAO", "CGB", "IGU", "NAT", "CGH", "MCZ", "JPA", "SLZ",
                "CWB", "AJU", "BPS", "TFF", "IOS", "PMW", "THE", "BVB", "RBR", "PNZ"*//*);*/

        LocalDate startDate = LocalDate.of(2025, 9, 10);
        LocalDate endDate = LocalDate.of(2025, 11, 20);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<CompletableFuture<Void>> tasks = new CopyOnWriteArrayList<>();

        for (String arrivalAirport : arrivalAirports) {
            System.out.println("🔍 Iniciando pesquisa para o destino: " + arrivalAirport);
            AtomicInteger dayCounter = new AtomicInteger(0);
            long totalDays = startDate.datesUntil(endDate.plusDays(1)).count();

            for (LocalDate currentDate = startDate; !currentDate.isAfter(endDate); currentDate = currentDate.plusDays(1)) {
                String departureTime = currentDate.format(formatter);
                int currentProgress = dayCounter.incrementAndGet();

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    System.out.println("📡 Pesquisando voos para " + arrivalAirport + " no dia " + departureTime + " (" + currentProgress + "/" + totalDays + ")");

                    String response = flightSearchService.searchAirlines(departureAirport, arrivalAirport, departureTime,1);
                    String searchId = flightSearchService.getSearchIdFromResponse(response);
                    List<String> airlines = flightSearchService.parseAirlines(response);
                    for (String airline : airlines) {
                        flightSearchService.searchFlightsByAirline(airline, searchId);
                    }

                    System.out.println("✅ Consulta finalizada para " + arrivalAirport + " no dia " + departureTime);
                }, executorService);

                tasks.add(future);
            }
        }

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

        executorService.shutdown();

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        LocalTime executionTime = LocalTime.ofSecondOfDay(duration.getSeconds());
        System.out.println("🏁 Duração total da execução: " + executionTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
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
