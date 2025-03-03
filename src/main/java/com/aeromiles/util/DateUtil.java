package com.aeromiles.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static io.micrometer.common.util.StringUtils.isBlank;
import static io.micrometer.common.util.StringUtils.isNotBlank;
import static java.util.Objects.isNull;

public class DateUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATTER_UTC = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    public static LocalDateTime stringToLocalDateTime(String data) {
        if (isNotBlank(data)) {
            try {
                return LocalDateTime.parse(data, FORMATTER);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Data e hora inválidas: " + data, e);
            }
        }
        return null;
    }

    public static String localDateTimeToString(LocalDateTime dateTime) {
        if (dateTime != null) {
            return dateTime.format(FORMATTER);
        }
        return null;
    }

    public static LocalDateTime stringToLocalDateTimeUTC(String data) {
        if (isNotBlank(data)) {
            try {
                return LocalDateTime.parse(data, FORMATTER_UTC);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Data e hora inválidas: " + data, e);
            }
        }
        return null;
    }

    public static LocalDate stringToLocalDate(String data) {
        if (isBlank(data)) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(data, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static LocalDate convertToLocalDate(LocalDateTime localDateTime) {
        if (isNull(localDateTime)) {
            return null;
        }
        return localDateTime.toLocalDate();
    }

    public static LocalDateTime convertToLocalDateTime(LocalDate localDate) {
        if (isNull(localDate)) {
            return null;
        }
        return localDate.atStartOfDay(); // Define a hora como 00:00:00
    }

    public static String stringToLocalDateString(String dataHora){
        if (isNotBlank(dataHora)) {
            try {
                return LocalDate.parse(dataHora, FORMATTER).toString();
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Data e hora inválidas: " + dataHora, e);
            }
        }
        return null;
    }

    public static String stringToTimeString(String dataHora){
        if (isNotBlank(dataHora)) {
            try {
                return LocalDateTime.parse(dataHora, FORMATTER).toLocalTime().toString();
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Data e hora inválidas: " + dataHora, e);
            }
        }
        return null;
    }
}
