package br.com.zalf.prolog.webservice.commons.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created on 20/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ProLogDateParser {
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER;
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER;

    static {
        DEFAULT_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
        DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }

    private ProLogDateParser() {
        throw new IllegalStateException(ProLogDateParser.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static LocalDate toLocalDate(@NotNull final String date) {
        Preconditions.checkNotNull(date);

        if (date.length() != DEFAULT_DATE_FORMAT.length()) {
            throw new IllegalArgumentException(String.format(
                    "The provided date %s is not in the expected format: %s",
                    date,
                    DEFAULT_DATE_FORMAT));
        } else {
            return LocalDate.parse(date, DEFAULT_DATE_FORMATTER);
        }
    }

    @NotNull
    public static LocalDateTime toLocalDateTime(@NotNull final String date) {
        Preconditions.checkNotNull(date);

        // - 2 para descontar as duas aspas simples ao redor do T. Elas NÃO devem ser enviadas nas datas em formato
        // String.
        if (date.length() != (DEFAULT_DATE_TIME_FORMAT.length() - 2)) {
            throw new IllegalArgumentException(String.format(
                    "The provided date-time %s is not in the expected format: %s",
                    date,
                    DEFAULT_DATE_TIME_FORMAT));
        } else {
            return LocalDateTime.parse(date, DEFAULT_DATE_TIME_FORMATTER);
        }
    }

    @NotNull
    public static String toString(@NotNull final LocalDate localDate) {
        Preconditions.checkNotNull(localDate);

        return localDate.format(DEFAULT_DATE_FORMATTER);
    }
}