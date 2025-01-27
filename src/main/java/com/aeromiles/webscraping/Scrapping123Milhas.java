package com.aeromiles.webscraping;

import com.aeromiles.model.onetwothree.FlightOneTwoThree;
import com.aeromiles.service.OneTwoThreeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


//@SpringBootApplication
//@ComponentScan(basePackages = "com.aeromiles")
public class Scrapping123Milhas {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Scrapping123Milhas.class, args);

        OneTwoThreeService oneTwoThreeService = context.getBean(OneTwoThreeService.class);

        long startTime = System.currentTimeMillis();

        String departureAirport = "BSB";
        List<String> arrivalAirports = List.of("GRU", "GIG", "CNF", "VCP", "SDU", "REC", "SSA", "POA", "FOR" /*
            "GRU", "GIG", "CNF", "VCP", "SDU", "REC", "SSA", "POA", "FOR", "BEL",
            "VIX", "GYN", "MAO", "CGB", "IGU", "NAT", "CGH", "MCZ", "JPA", "SLZ",
            "CWB", "AJU", "BPS", "TFF", "FLN", "IOS", "PMW", "THE", "BVB", "RBR", "PNZ"*/
        );

        LocalDate startDate = LocalDate.of(2025, 2, 1);
        LocalDate endDate = LocalDate.of(2025, 3, 31);

        int threadPoolSize = 1; // Número de threads para paralelismo

        Map<String, List<FlightOneTwoThree>> cheapestFlightsByAirport = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (String arrivalAirport : arrivalAirports) {
            System.out.println("Iniciando pesquisa para o destino: " + arrivalAirport);

            AtomicInteger dayCounter = new AtomicInteger(0);
            long totalDays = startDate.datesUntil(endDate.plusDays(1)).count();

            for (LocalDate currentDate = startDate; !currentDate.isAfter(endDate); currentDate = currentDate.plusDays(1)) {
                String departureTime = currentDate.format(formatter);
                int currentProgress = dayCounter.incrementAndGet();
                System.out.println("Pesquisando voos para " + arrivalAirport + " no dia " + departureTime + " (" + currentProgress + "/" + totalDays + ")");

                try {
                    List<FlightOneTwoThree> flights = oneTwoThreeService.save(departureAirport, arrivalAirport, departureTime, 1, 0, 0, 3, 0);

                    flights.stream()
                        .filter(flight -> flight.getMiles() > 0)
                        .min(Comparator.comparingInt(FlightOneTwoThree::getMiles))
                        .ifPresent(flight ->
                            System.out.println("Voo encontrado para " + arrivalAirport + " no dia " + departureTime + ": " + flight.getMiles() + " milhas."));
                } catch (Exception e) {
                    System.err.println("Erro ao buscar voos para " + arrivalAirport + " no dia " + departureTime + ": " + e.getMessage());
                }
            }

            //cheapestFlightsByAirport.put(arrivalAirport, allFlights);
        }

        System.out.println("Resumo dos 5 voos mais baratos em milhas por destino:");
        for (Map.Entry<String, List<FlightOneTwoThree>> entry : cheapestFlightsByAirport.entrySet()) {
            String arrivalAirport = entry.getKey();
            List<FlightOneTwoThree> flights = entry.getValue();

            System.out.println("Destino: " + arrivalAirport);
            if (flights.isEmpty()) {
                System.out.println("  Nenhum voo encontrado.");
            } else {
                for (FlightOneTwoThree flight : flights) {
                    System.out.println("  Voo: " + flight);
                }
            }
        }

        long endTime = System.currentTimeMillis(); // Finaliza a contagem do tempo
        long elapsedTimeMinutes = (endTime - startTime) / (1000 * 60); // Converte para minutos
        System.out.println("Tempo total de execução: " + elapsedTimeMinutes + " minutos.");

        System.exit(0); // Encerra a execução
    }

        /*for (String arrivalAirport : arrivalAirports) {
            System.out.println("Iniciando pesquisa para o destino: " + arrivalAirport);

            List<CompletableFuture<List<FlightOneTwoThree>>> futures = new ArrayList<>();
            AtomicInteger dayCounter = new AtomicInteger(0);
            long totalDays = startDate.datesUntil(endDate.plusDays(1)).count();

            for (LocalDate currentDate = startDate; !currentDate.isAfter(endDate); currentDate = currentDate.plusDays(1)) {
                String departureTime = currentDate.format(formatter);
                futures.add(CompletableFuture.supplyAsync(() -> {
                    int currentProgress = dayCounter.incrementAndGet();
                    System.out.println("Pesquisando voos para " + arrivalAirport + " no dia " + departureTime + " (" + currentProgress + "/" + totalDays + ")");
                    try {
                        List<FlightOneTwoThree> flights = oneTwoThreeService.save(departureAirport, arrivalAirport, departureTime, 1, 0, 0, 3, 0);

                        flights.stream()
                            .filter(flight -> flight.getMiles() > 0)
                            .min(Comparator.comparingInt(FlightOneTwoThree::getMiles))
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

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            List<FlightOneTwoThree> allFlights = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .filter(flight -> flight.getMiles() > 0)
                .sorted(Comparator.comparingInt(FlightOneTwoThree::getMiles)) // Ordena por milhas
                .limit(5) // Mantém os 5 voos mais baratos
                .collect(Collectors.toList());

            cheapestFlightsByAirport.put(arrivalAirport, allFlights);
            }

            // Exibe os 5 voos mais baratos para cada destino
            System.out.println("Resumo dos 5 voos mais baratos em milhas por destino:");
            for (Map.Entry<String, List<FlightOneTwoThree>> entry : cheapestFlightsByAirport.entrySet()) {
                String arrivalAirport = entry.getKey();
                List<FlightOneTwoThree> flights = entry.getValue();

                System.out.println("Destino: " + arrivalAirport);
                if (flights.isEmpty()) {
                    System.out.println("  Nenhum voo encontrado.");
                } else {
                    for (FlightOneTwoThree flight : flights) {
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
