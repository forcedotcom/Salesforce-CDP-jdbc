package com.salesforce.cdp.queryservice.util;


import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;


public class DateTimeUtilTest {

    @Test
    public void testUtcDateParsing() throws ParseException {
        Date date = DateTimeUtil.getDate("2022-10-17 21:52:18.001 UTC");
        assertThat(date.toLocalDate()).isEqualTo(LocalDate.parse("2022-10-17"));
    }

    @Test
    public void testIstDateParsing() throws ParseException {
        Date date = DateTimeUtil.getDate("2022-10-17 21:52:18.001 Asia/Kolkata");
        assertThat(date.toLocalDate()).isEqualTo(LocalDate.parse("2022-10-17"));
    }

    @Test
    public void testUtcTimestampParsing() throws ParseException {
        Timestamp timestamp = DateTimeUtil.getTimestamp("2022-10-17 21:52:18.001 UTC");
        assertThat(timestamp.toLocalDateTime()).isEqualTo(LocalDateTime.parse("2022-10-17T21:52:18.001"));
    }

    @Test
    public void testIstTimestampParsing() throws ParseException {
        Timestamp timestamp = DateTimeUtil.getTimestamp("2022-10-17 21:52:18.001 Asia/Kolkata");
        assertThat(timestamp.toLocalDateTime()).isEqualTo(LocalDateTime.parse("2022-10-17T21:52:18.001"));
    }

    @Test
    public void testTimestampWithOffsetParsing() throws ParseException {
        Timestamp timestamp = DateTimeUtil.getTimestamp("2022-10-17 21:52:18.001 +05:30");
        assertThat(timestamp.toLocalDateTime()).isEqualTo(LocalDateTime.parse("2022-10-17T21:52:18.001"));
    }

    @Test
    public void testTimestampWithOffsetParsingWoSpace() throws ParseException {
        Timestamp timestamp = DateTimeUtil.getTimestamp("2022-10-17 21:52:18.001+05:30");
        assertThat(timestamp.toLocalDateTime()).isEqualTo(LocalDateTime.parse("2022-10-17T21:52:18.001"));
    }


    @Test
    public void testDateParsing() throws ParseException {
        Date date = DateTimeUtil.getDate("2022-10-17");
        assertThat(date.toLocalDate()).isEqualTo(LocalDate.parse("2022-10-17"));
    }

    @Test
    public void testIsoDateParsing() throws ParseException {
        Date date = DateTimeUtil.getDate("2022-10-17T21:52:18.001");
        assertThat(date.toLocalDate()).isEqualTo(LocalDate.parse("2022-10-17"));
    }

    @Test
    public void testIsoDateTimeParsing() throws ParseException {
        Timestamp date = DateTimeUtil.getTimestamp("2022-10-17T21:52:18.001");
        assertThat(date.toLocalDateTime()).isEqualTo(LocalDateTime.parse("2022-10-17T21:52:18.001"));
    }

    @Test
    public void testZoneConversionForTimestamp() {
        Timestamp timestamp = DateTimeUtil.getTimestamp("2022-10-17T21:52:18.001 UTC", ZoneId.of("UTC"), Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Asia/Kolkata"))));
        assertThat(timestamp.toLocalDateTime()).isEqualTo(LocalDateTime.parse("2022-10-18T03:22:18.001"));
    }

    @Test
    public void testZoneConversionForTime() {
        Time time = DateTimeUtil.getTime("2022-10-17T21:52:18.001 UTC", ZoneId.of("UTC"), Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Asia/Kolkata"))));
        assertThat(time.toLocalTime()).isEqualTo(LocalTime.parse("03:22:18"));
    }

    @Test
    public void testZoneConversionForTimeWithoutDate() {
        Time time = DateTimeUtil.getTime("21:52:18.001 UTC", ZoneId.of("UTC"), Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Asia/Kolkata"))));
        assertThat(time.toLocalTime()).isEqualTo(LocalTime.parse("03:22:18"));
    }

    @Test
    public void testNullForTimestampHandling() {
        Timestamp timestamp = DateTimeUtil.getTimestamp(null);
        assertThat(timestamp).isNull();
    }

    @Test
    public void testNullForDateHandling() {
        Date date = DateTimeUtil.getDate(null);
        assertThat(date).isNull();
    }

    @Test
    public void testNullForTimeHandling() {
        Time time = DateTimeUtil.getTime(null);
        assertThat(time).isNull();
    }
}