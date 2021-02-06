package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created on 08/02/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FormatUtils {
    private static final DateTimeFormatter USER_FRIENDLY_DATE_TIME_FORMAT;

    static {
        USER_FRIENDLY_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    }

    private FormatUtils() {
        throw new IllegalStateException(FormatUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static String toUserFriendlyDateTime(@NotNull final LocalDateTime localDateTime) {
        return localDateTime.format(USER_FRIENDLY_DATE_TIME_FORMAT);
    }

    @NotNull
    public static String truncateToString(@NotNull final BigDecimal number, @NotNull final Integer casasDecimais) {
        return number.setScale(casasDecimais, RoundingMode.CEILING).toString();
    }
}