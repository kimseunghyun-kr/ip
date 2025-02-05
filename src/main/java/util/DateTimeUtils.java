package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import exceptions.UserFacingException;

/**
 * Utility class for handling date and date-time parsing.
 * <p>
 * This class provides helper methods to parse date or date-time strings
 * into {@link LocalDateTime} objects.
 * </p>
 */
public class DateTimeUtils {

    /**
     * Parses a date or date-time string into a {@link LocalDateTime} object.
     * <p>
     * If the input string contains a "T" character, it is assumed to be a
     * full date-time string and is parsed as {@code LocalDateTime}.
     * Otherwise, it is treated as a date-only string and converted to
     * {@code LocalDateTime} at the start of the day.
     * </p>
     *
     * @param input The date or date-time string in ISO-8601 format.
     * @return A {@link LocalDateTime} representing the parsed date and time.
     * @throws UserFacingException If the input string cannot be parsed.
     */
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
