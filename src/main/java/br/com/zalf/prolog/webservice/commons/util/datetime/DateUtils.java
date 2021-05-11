package br.com.zalf.prolog.webservice.commons.util.datetime;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class DateUtils {

    private DateUtils() {
        throw new UnsupportedOperationException("An util class cannot be instantiated!");
    }

    @NotNull
    public static Timestamp toTimestamp(@NotNull final Date dateToConvert) {
        return new Timestamp(dateToConvert.getTime());
    }

    @NotNull
    public static java.sql.Date toSqlDate(@NotNull final Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime());
    }

    @NotNull
    public static java.sql.Date toSqlDate(@NotNull final LocalDate localDateToConvert) {
        return java.sql.Date.valueOf(localDateToConvert);
    }

    @NotNull
    public static LocalDate toLocalDate(@NotNull final java.sql.Date sqlDateToConvert) {
        return sqlDateToConvert.toLocalDate();
    }

    @NotNull
    public static LocalDate toLocalDate(@NotNull final Date dateToConvert) {
        return toSqlDate(dateToConvert).toLocalDate();
    }

    @NotNull
    public static LocalDate toLocalDate(final long dateInMillisToConvert, @NotNull final ZoneId zoneId) {
        return Instant
                .ofEpochMilli(dateInMillisToConvert)
                .atZone(zoneId)
                .toLocalDate();
    }

    @NotNull
    public static LocalDateTime toLocalDateTime(@NotNull final Date dateToConvert) {
        final Instant instant = Instant.ofEpochMilli(dateToConvert.getTime());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @NotNull
    public static LocalDate validateAndParse(@NotNull final String dateToParse,
                                             @NotNull final DateTimeFormatter formatter) {
        return LocalDate.parse(dateToParse, formatter);
    }

    @NotNull
    public static LocalDate parseDate(@NotNull final String dateString) {
        return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @NotNull
    public static Date getPrimeiroDiaMes(@NotNull final Date date) {
        final Calendar firstDay = Calendar.getInstance();
        firstDay.setTime(DateUtils.toSqlDate(date));
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        return new java.sql.Date(firstDay.getTimeInMillis());
    }

    @NotNull
    public static Date getUltimoDiaMes(@NotNull final Date date) {
        final Calendar lastDay = Calendar.getInstance();
        lastDay.setTime(DateUtils.toSqlDate(date));
        lastDay.set(Calendar.DAY_OF_MONTH, 1);
        lastDay.add(Calendar.MONTH, 1);
        lastDay.add(Calendar.DAY_OF_MONTH, -1);
        return new java.sql.Date(lastDay.getTimeInMillis());
    }

    public static long secondsBetween(final long startDateInMillis, final long endDateInMillis) {
        return TimeUnit.MILLISECONDS.toSeconds(endDateInMillis - startDateInMillis);
    }

    public static boolean verificaAno(@NotNull final Date dataASerVerificada,
                                      final int anoMaximoPermitido,
                                      final int anoMinimoPermitido) {
        final SimpleDateFormat anoFormat = new SimpleDateFormat("yyyy");
        final int anoExtraido = Integer.parseInt(anoFormat.format(dataASerVerificada));
        return anoExtraido >= anoMaximoPermitido || anoExtraido <= anoMinimoPermitido;
    }

    public static boolean verificaAno(final LocalDate dataASerVerificada,
                                      final int anoMaximoPermitido,
                                      final int anoMinimoPermitido) {
        final int anoExtraido = dataASerVerificada.getYear();
        return anoExtraido >= anoMaximoPermitido || anoExtraido <= anoMinimoPermitido;
    }

    public static boolean isAfterNDays(@NotNull final LocalDateTime date, final long nthDay) {
        return ChronoUnit.DAYS.between(date, LocalDateTime.now()) > nthDay;
    }
}
