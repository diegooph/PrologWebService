package br.com.zalf.prolog.webservice.commons.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    /**
     * Converte uma data no formato java.util.Date para java.sql.Timestamp
     *
     * @param utilDate
     * @return java.sql.Timestamp
     */
    public static Timestamp toTimestamp(Date utilDate) {
        return new Timestamp(utilDate.getTime());
    }

    /**
     * Converte uma data no formato java.util.Date para java.sql.Date
     *
     * @param utilDate
     * @return java.sql.Date
     */
    public static java.sql.Date toSqlDate(Date utilDate) {
        return new java.sql.Date(utilDate.getTime());
    }

    /**
     * Converte um java.time.LocalDate para java.sql.Date
     *
     * @param localDate
     * @return java.sql.Date
     */
    public static java.sql.Date toSqlDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    /**
     * Converte um java.sql.Date para java.time.LocalDate
     *
     * @param sqlDate
     * @return java.time.LocalDate
     */
    public static LocalDate toLocalDate(java.sql.Date sqlDate) {
        return sqlDate.toLocalDate();
    }

    /**
     * Converte um java.util.date para LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime res = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return res;
    }

    /**
     * Converte um java.util.Date para java.time.LocalDate
     *
     * @param utilDate
     * @return
     */
    public static LocalDate toLocalDate(Date utilDate) {
        return toSqlDate(utilDate).toLocalDate();
    }

    public static LocalDate toLocalDate(final long dateMillis, @NotNull final ZoneId zoneId) {
        return Instant
                .ofEpochMilli(dateMillis)
                .atZone(zoneId)
                .toLocalDate();
    }

    @NotNull
    public static LocalDate validateAndParse(@NotNull final String date, @NotNull final DateTimeFormatter formatter) {
        Preconditions.checkNotNull(date);
        return LocalDate.parse(date, formatter);
    }

    /**
     * Retorna um date com o primerio dia do mes inserido
     *
     * @param date
     * @return
     */
    public static Date getPrimeiroDiaMes(Date date) {

        Calendar first = Calendar.getInstance();
        first.setTime(DateUtils.toSqlDate(date));
        first.set(Calendar.DAY_OF_MONTH, 1);
        return new java.sql.Date(first.getTimeInMillis());
    }

    /**
     * Retorna um date com o ultimo dia do mes inserido
     *
     * @param date
     * @return
     */
    public static Date getUltimoDiaMes(Date date) {

        Calendar last = Calendar.getInstance();
        last.setTime(DateUtils.toSqlDate(date));
        last.set(Calendar.DAY_OF_MONTH, 1);
        last.add(Calendar.MONTH, 1);
        last.add(Calendar.DAY_OF_MONTH, -1);

        return new java.sql.Date(last.getTimeInMillis());
    }

    public static long secondsBetween(final long startDateMillis, final long endDateMillis) {
        return TimeUnit.MILLISECONDS.toSeconds(endDateMillis - startDateMillis);
    }

    /**
     * Método que verifica se um ano está dentro do limite estabelecido.
     *
     * @param data               - data a ser analisada.
     * @param anoMaximoPermitido - ano máximo permitido.
     * @param anoMinimoPermitido - ano mínimo permitido.
     * @return true caso o ano esteja fora do limite estabelecido.
     */
    public static boolean verificaAno(Date data, int anoMaximoPermitido, int anoMinimoPermitido) {
        SimpleDateFormat ano = new SimpleDateFormat("yyyy");
        final int anoData = Integer.parseInt(ano.format(data));
        return anoData >= anoMaximoPermitido || anoData <= anoMinimoPermitido;
    }

    /**
     * Método que verifica se um ano está dentro do limite estabelecido.
     *
     * @param data               - data a ser analisada.
     * @param anoMaximoPermitido - ano máximo permitido.
     * @param anoMinimoPermitido - ano mínimo permitido.
     * @return true caso o ano esteja fora do limite estabelecido.
     */
    public static boolean verificaAno(LocalDate data, int anoMaximoPermitido, int anoMinimoPermitido) {
        final int anoData = data.getYear();
        return anoData >= anoMaximoPermitido || anoData <= anoMinimoPermitido;
    }
}
