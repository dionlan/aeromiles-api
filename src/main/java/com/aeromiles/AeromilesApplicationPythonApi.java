package com.aeromiles;

import com.aeromiles.service.SmilesApiService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@SpringBootApplication
public class AeromilesApplicationPythonApi {
	private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

	public static void main(String[] args) throws Exception {
		ApplicationContext context = SpringApplication.run(AeromilesApplicationPythonApi.class, args);
		SmilesApiService smilesApiService = context.getBean(SmilesApiService.class);

		System.out.println("\n🛫🛫🛫 BUSCANDO VOOS... 🛫🛫🛫");

		// Valores hardcoded para teste
		final String origin = "BSB";
		final String destination = "REC";  // Ou "CGH" se for o caso
		final String date = "2025-08-02";

		smilesApiService.searchFlights(origin, destination, date).ifPresentOrElse(
			json -> {
				try {
					JsonNode flightSegments = json.path("requestedFlightSegmentList");
					int totalFlights = countFlights(flightSegments);

					if (totalFlights == 0) {
						System.out.println("\n😢 Nenhum voo encontrado para esta rota e data");
					} else {
						System.out.printf("\n🔍 %d opções de voo encontradas%n", totalFlights);
						System.out.printf("✨ Voos disponíveis de %s para %s em %s:%n", origin, destination, formatDate(date));
						System.out.println("=".repeat(60));

						for (JsonNode segment : flightSegments) {
							JsonNode flightList = segment.path("flightList");
							for (JsonNode flight : flightList) {
								printFlightInfo(flight);
								System.out.println("\n" + "-".repeat(60) + "\n");
							}
						}
					}
				} catch (Exception e) {
					System.out.println("\n❌ Erro ao processar resultados: " + e.getMessage());
				}
			},
			() -> System.out.println("\n⚠ Nenhum voo encontrado ou ocorreu um erro na consulta")
		);
	}

	private static int countFlights(JsonNode flightSegments) {
		int count = 0;
		for (JsonNode segment : flightSegments) {
			count += segment.path("flightList").size();
		}
		return count;
	}

	private static void printFlightInfo(JsonNode flight) {
		String airline = flight.path("airline").path("name").asText();
		String flightNumber = flight.path("legList").get(0).path("flightNumber").asText();
		String departureAirport = flight.path("departure").path("airport").path("code").asText();
		String arrivalAirport = flight.path("arrival").path("airport").path("code").asText();
		String departureTime = formatDateTime(flight.path("departure").path("date").asText());
		String arrivalTime = formatDateTime(flight.path("arrival").path("date").asText());
		String duration = formatDuration(flight.path("duration"));
		int stops = flight.path("stops").asInt();
		String cabin = flight.path("cabin").asText();

		boolean specialFlight = flight.path("fareList").elements().next().path("miles").asInt() == 0;

		// Extrai informações de preço
		PriceInfo priceInfo = extractPriceInfo(flight.path("fareList"));

		System.out.printf("""
        %s%s %s %s → %s
        💺 Classe: %s
        %s Voo: %s
        🛫 Origem: %s
            🕓 Partida: %s
        🛬 Destino: %s
            🕔 Chegada: %s
        ⏳ Duração: %s
        %s
        %s""",
			specialFlight ? "🥅 " : "",
			airline, flightNumber, departureAirport, arrivalAirport,
			cabin.equals("ECONOMIC") ? "Econômica" : "Executiva",
			stops == 0 ? "✈" : "🛬", stops == 0 ? "Direto" : stops + " Parada" + (stops > 1 ? "s" : ""),
			departureAirport, departureTime,
			arrivalAirport, arrivalTime,
			duration,
			priceInfo.hasMiles ? "🎟 Milhas: " + formatMiles(priceInfo.miles) : "🎟 Milhas: (pagamento somente em reais)",
			priceInfo.hasMoney ? "💰 " + formatCurrency(priceInfo.money) : "");
	}

	private static class PriceInfo {
		int miles;
		double money;
		boolean hasMiles;
		boolean hasMoney;

		PriceInfo(int miles, double money, boolean hasMiles, boolean hasMoney) {
			this.miles = miles;
			this.money = money;
			this.hasMiles = hasMiles;
			this.hasMoney = hasMoney;
		}
	}

	private static PriceInfo extractPriceInfo(JsonNode fareList) {
		int bestMiles = 0;
		double bestMoney = 0;
		boolean hasMiles = false;
		boolean hasMoney = false;

		if (!fareList.isMissingNode() && fareList.isArray()) {
			for (JsonNode fare : fareList) {
				String type = fare.path("type").asText();

				if (type.equals("SMILES_CLUB") || type.equals("SMILES")) {
					int miles = fare.path("miles").asInt();
					if (miles > 0 && (!hasMiles || miles < bestMiles)) {
						bestMiles = miles;
						hasMiles = true;
					}
				} else if (type.equals("MONEY")) {
					double money = fare.path("money").asDouble();
					if (money > 0) {
						double total = fare.path("airlineFare").asDouble();
						if (fare.has("g3") && fare.path("g3").has("costTax")) {
							total += fare.path("g3").path("costTax").asDouble();
						}
						if (!hasMoney || total < bestMoney) {
							bestMoney = total;
							hasMoney = true;
						}
					}
				}
			}
		}

		return new PriceInfo(bestMiles, bestMoney, hasMiles, hasMoney);
	}

	private static String formatMiles(int miles) {
		return String.format("%,d", miles).replace(",", ".");
	}

	private static String formatCurrency(double value) {
		return CURRENCY_FORMAT.format(value).trim();
	}

	private static String formatDuration(JsonNode durationNode) {
		int hours = durationNode.path("hours").asInt();
		int minutes = durationNode.path("minutes").asInt();
		return String.format("%dh%02dm", hours, minutes);
	}

	private static String formatDateTime(String isoDateTime) {
		try {
			LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_DATE_TIME);
			return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		} catch (Exception e) {
			return isoDateTime;
		}
	}

	private static String formatDate(String isoDateTime) {
		try {
			LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_DATE_TIME);
			return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		} catch (Exception e) {
			return isoDateTime;
		}
	}
}
