package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static LocalDateTime parseDateOrDateTime(String input) {
        if (input.contains("T")) {
            // If input has time, parse as LocalDateTime
            return LocalDateTime.parse(input);
        } else {
            // If input has only date, parse as LocalDate and convert to LocalDateTime
            return LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        }
    }
}
