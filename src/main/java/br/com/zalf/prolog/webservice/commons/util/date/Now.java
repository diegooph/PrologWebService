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

    public static long utcMillis() {
        return System.currentTimeMillis();
    }

    @NotNull
    public static Timestamp timestampUtc() {
        return new Timestamp(utcMillis());
    }

    @NotNull
    public static LocalDate localDateUtc() {
        return LocalDate.now(Clock.systemUTC());
    }

    @NotNull
    public static LocalDateTime localDateTimeUtc() {
        return LocalDateTime.now(Clock.systemUTC());
    }

    @NotNull
    public static OffsetDateTime offsetDateTimeUtc() {
        return OffsetDateTime.now(Clock.systemUTC());
    }

    @NotNull
    public static ZonedDateTime zonedDateTimeTzAware(@NotNull final ZoneId zoneId) {
        return ZonedDateTime.now(zoneId);
    }
}