package br.com.zalf.prolog.webservice.commons.util;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Created on 04/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class LocalDateFactory {

    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE;

    static {
        FORMATTER_CACHE = new ImmutableMap.Builder<String, DateTimeFormatter>()
                .put("yyyy-MM-dd", DateTimeFormatter.ISO_DATE)
                .build();
    }

    private LocalDateFactory() {

    }

    public static LocalDate createFromFormat(@NotNull final String date, @NotNull final String format) {
        final DateTimeFormatter formatter = FORMATTER_CACHE.get(format);
        if (formatter == null) {
            throw new IllegalArgumentException("No formatter available to deal with the requested format: " + format);
        }

        return LocalDate.parse(date, formatter);
    }
}