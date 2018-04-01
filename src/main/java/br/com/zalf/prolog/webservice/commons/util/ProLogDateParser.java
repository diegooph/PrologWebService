package br.com.zalf.prolog.webservice.commons.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created on 20/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ProLogDateParser {
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER;

    static {
        DEFAULT_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    }

    private ProLogDateParser() {
        throw new IllegalStateException(ProLogDateParser.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static LocalDate validateAndParse(@NotNull final String date) {
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
    public static String toString(@NotNull final LocalDate localDate) {
        Preconditions.checkNotNull(localDate);

        return localDate.format(DEFAULT_DATE_FORMATTER);
    }
}