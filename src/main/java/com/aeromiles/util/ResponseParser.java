package com.aeromiles.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

public class ResponseParser {

    // Método para extrair o 'searchId' da resposta
    public static String extractSearchId(String response) {
        // Parse o JSON da resposta
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        // Acessar diretamente o campo "searchIntention" e obter o id
        if (jsonObject.has("props")) { //pageProps //
            JsonObject props = jsonObject.getAsJsonObject("props");

            if (props.has("pageProps")) {
                JsonObject pageProps = props.getAsJsonObject("pageProps");

                if (pageProps.has("searchIntention")) {
                    JsonObject searchIntention = pageProps.getAsJsonObject("searchIntention");

                    // Acessa diretamente o campo "id" dentro de "searchIntention"
                    if (searchIntention.has("id")) {
                        return searchIntention.get("id").getAsString();
                    }
                }
            }
        }
        // Caso o campo "searchIntention" ou "id" não seja encontrado, lançar uma exceção
        throw new RuntimeException("searchIntention or id not found in the response");
    }

    // Método para extrair a lista de companhias aéreas
    public static List<String> extractAirlines(String response) {
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        List<String> airlinesList = new ArrayList<>();
        if (jsonObject.has("props")) { //pageProps //
            JsonObject props = jsonObject.getAsJsonObject("props");

            if (props.has("pageProps")) {
                JsonObject pageProps = props.getAsJsonObject("pageProps");

                if (pageProps.has("searchIntention")) {
                    JsonObject searchIntention = pageProps.getAsJsonObject("searchIntention");

                    if (searchIntention.has("airlines")) {
                        var airlinesArray = searchIntention.getAsJsonArray("airlines");

                        airlinesArray.forEach(element -> {
                            if (element.getAsJsonObject().has("label") && element.getAsJsonObject().get("status").getAsJsonObject().has("enable")) {
                                boolean isEnabled = element.getAsJsonObject().get("status").getAsJsonObject().get("enable").getAsBoolean();
                                if (isEnabled) {
                                    airlinesList.add(element.getAsJsonObject().get("label").getAsString());
                                }
                            }
                        });
                    }
                }
            }
            return airlinesList;
        }
        throw new RuntimeException("No airlines found in the response");
    }
}
