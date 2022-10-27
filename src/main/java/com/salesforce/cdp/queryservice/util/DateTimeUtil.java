package com.salesforce.cdp.queryservice.util;


import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Optional;

import static java.time.temporal.ChronoField.*;

@Slf4j
public class DateTimeUtil {

    private final static DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .optionalStart()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(DAY_OF_MONTH, 2)
            .optionalEnd()
            .optionalStart()
            .optionalStart()
            .appendLiteral(" ")
            .optionalEnd()
            .optionalStart()
            .appendLiteral("T")
            .optionalEnd()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(":")
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(":")
            .appendValue(SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendLiteral(".")
            .appendValue(MILLI_OF_SECOND, 3)
            .optionalEnd()
            .optionalStart()
            .appendLiteral(" ")
            .appendZoneText(TextStyle.FULL)
            .optionalEnd()
            .optionalStart()
            .appendOffset("+HH:MM", "+00:00")
            .optionalEnd()
            .optionalStart()
            .appendOffset("+HHMM", "+0000")
            .optionalEnd()
            .optionalEnd()
            .toFormatter();

    public static Timestamp getTimestamp(String stringValue, ZoneId currentZoneId, Calendar cal) {
        if (stringValue == null) {
            return null;
        }
        ZoneId zoneId = cal.getTimeZone().toZoneId();
        LocalDateTime localDateTime = safelyParseDateTimeString(stringValue);
        if (localDateTime == null) {
            return null;
        }
        LocalDateTime localDateTimeAtNewZone = localDateTime
                .atZone(currentZoneId)
                .withZoneSameInstant(zoneId)
                .toLocalDateTime();
        return Timestamp.valueOf(localDateTimeAtNewZone);
    }

    public static Time getTime(String stringValue, ZoneId currentZoneId, Calendar cal) {
        if (stringValue == null) {
            return null;
        }
        ZoneId zoneId = cal.getTimeZone().toZoneId();
        LocalTime localTime = safelyParseTimeString(stringValue);
        if (localTime == null) {
            return null;
        }
        LocalTime localTimeAtNewZone = LocalDateTime.of(LocalDate.now(currentZoneId), localTime)
                .atZone(currentZoneId)
                .withZoneSameInstant(zoneId)
                .toLocalTime();
        return Time.valueOf(localTimeAtNewZone);
    }
    public static Date getDate(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        LocalDate localDate = safelyParseDateString(stringValue);
        if (localDate == null) {
            return null;
        }
        return Date.valueOf(localDate);
    }

    public static Timestamp getTimestamp(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        LocalDateTime localDateTime = safelyParseDateTimeString(stringValue);
        if (localDateTime == null) {
            return null;
        }
        return Timestamp.valueOf(localDateTime);
    }

    public static Time getTime(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        LocalTime localTime = safelyParseTimeString(stringValue);
        if (localTime == null) {
            return null;
        }
        return Time.valueOf(localTime);
    }

    private static LocalDateTime safelyParseDateTimeString(String stringValue) {
        try {
            return LocalDateTime.parse(stringValue, FORMATTER);
        } catch (Exception e) {
            log.trace("Failed parsing Timestamp " + stringValue, e);
        }
        return null;
    }

    private static LocalDate safelyParseDateString(String stringValue) {
        try {
            return LocalDate.parse(stringValue, FORMATTER);
        } catch (Exception e) {
            log.trace("Failed parsing date" + stringValue, e);
        }
        return null;
    }

    private static LocalTime safelyParseTimeString(String stringValue) {
        try {
            return LocalTime.parse(stringValue, FORMATTER);
        } catch (Exception e) {
            log.trace("Failed parsing date" + stringValue, e);
        }
        return null;
    }
}
