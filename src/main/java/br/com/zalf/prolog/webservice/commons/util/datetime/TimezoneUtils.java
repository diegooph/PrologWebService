package br.com.zalf.prolog.webservice.commons.util.datetime;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Created on 2021-04-26
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class TimezoneUtils {
    @NotNull
    public static LocalDateTime applyTimezone(@NotNull final LocalDateTime localDateTimeToApplyTimezone,
                                              @NotNull final ZoneId timezoneId) {
        return localDateTimeToApplyTimezone.atOffset(ZoneOffset.UTC).atZoneSameInstant(timezoneId).toLocalDateTime();
    }
}
