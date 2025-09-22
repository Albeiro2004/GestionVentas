package com.ventas.minipos.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static String humanReadableTime(Instant instant) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        Duration diff = Duration.between(dateTime, LocalDateTime.now());

        if (diff.toMinutes() < 1) return "Justo ahora";
        if (diff.toMinutes() < 60) return "Hace " + diff.toMinutes() + " minutos";
        if (diff.toHours() < 24) return "Hace " + diff.toHours() + " horas";
        return "El " + dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
