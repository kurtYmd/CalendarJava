package com.calendar.utils;

import java.time.format.DateTimeFormatter;

public class DateUtils {
    // Define a standard date formatter
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Private constructor to prevent instantiation
    private DateUtils() {}
}
