package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import exceptions.UserFacingException;

public class DateTimeUtils {

    public static LocalDateTime parseDateOrDateTime(String input) {
        try {
            if (input.contains("T")) {
                // If input has time, parse as LocalDateTime
                return LocalDateTime.parse(input);
            } else {
                // If input has only date, parse as LocalDate and convert to LocalDateTime
                return LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
            }
        } catch (DateTimeParseException e) {
            throw new UserFacingException(e.getMessage());
        }
    }
}
