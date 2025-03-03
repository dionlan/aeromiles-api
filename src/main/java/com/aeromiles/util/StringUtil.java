package com.aeromiles.util;

public class StringUtil {

    public static String extractAirlineName(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        int lastColonIndex = input.lastIndexOf(':');
        if (lastColonIndex == -1 || lastColonIndex == input.length() - 1) {
            throw new IllegalArgumentException("A string de entrada não está no formato esperado 'id:nome'.");
        }

        return input.substring(lastColonIndex + 1).trim();
    }
}
