package br.com.zalf.prolog.webservice.commons.util.date;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.*;

/**
 * Created on 04/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class Now {
    private Now() {
        throw new IllegalStateException(Now.class.getSimpleName() + " cannot be instantiated!");
    }

    public static long getUtcMillis() {
        return System.currentTimeMillis();
    }

    @NotNull
    public static Timestamp getTimestampUtc() {
        return new Timestamp(getUtcMillis());
    }

    @NotNull
    public static LocalDate getLocalDateUtc() {
        return LocalDate.now(Clock.systemUTC());
    }

    @NotNull
    public static LocalDateTime getLocalDateTimeUtc() {
        return LocalDateTime.now(Clock.systemUTC());
    }

    @NotNull
    public static OffsetDateTime getOffsetDateTimeUtc() {
        return OffsetDateTime.now(Clock.systemUTC());
    }

    @NotNull
    public static ZonedDateTime getZonedDateTimeTzAware(@NotNull final ZoneId zoneId) {
        return ZonedDateTime.now(zoneId);
    }
}