package br.com.zalf.prolog.webservice.commons.util.date;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * Created on 08/12/15
 *
 * @author Jean Zart (https://github.com/jeanzart)
 */
public class TimeUtils {
    private TimeUtils() {
        throw new IllegalStateException(TimeUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static Time differenceBetween(@NotNull final Time time1, final Time time2) {
        final LocalTime localTime1 = TimeUtils.toLocalTime(time1);
        final LocalTime localTime2 = TimeUtils.toLocalTime(time2);
        return TimeUtils.toSqlTime(localTime1.minus(localTime2.getLong(ChronoField.MILLI_OF_DAY), ChronoUnit.MILLIS));
    }

    @NotNull
    public static Time somaHoras(@NotNull final Time originalTime, final Time timeToBeAdded) {
        final LocalTime originalTimeConverted = TimeUtils.toLocalTime(originalTime);
        final LocalTime timeToBeAddedConverted = TimeUtils.toLocalTime(timeToBeAdded);
        return TimeUtils.toSqlTime(
                originalTimeConverted.plus(
                        timeToBeAddedConverted.getLong(ChronoField.MILLI_OF_DAY), ChronoUnit.MILLIS));
    }

    @NotNull
    public static LocalTime toLocalTime(@NotNull final Time sqlTime) {
        return sqlTime.toLocalTime();
    }

    @NotNull
    public static LocalTime toLocalTime(@NotNull final Timestamp timeStamp) {
        return timeStamp.toLocalDateTime().toLocalTime();
    }

    @NotNull
    public static Time toSqlTime(@NotNull final LocalTime localTime) {
        return Time.valueOf(localTime);
    }

    @NotNull
    public static Time toSqlTime(@NotNull final Timestamp timestamp) {
        return new Time(timestamp.getTime());
    }

    @NotNull
    public static Time toSqlTime(@NotNull final String stringToParse) {
        return toSqlTime(LocalTime.parse(stringToParse));
    }

    @NotNull
    public static Time timeFromDuration(@NotNull final Duration duration) {
        final long segundos = Math.abs(duration.getSeconds());
        return TimeUtils.toSqlTime(LocalTime.ofSecondOfDay(segundos));
    }

    @NotNull
    public static Duration durationFromTime(@NotNull final Time time) {
        final LocalTime localTime = TimeUtils.toLocalTime(time);
        final long segundos = localTime.getSecond() + (localTime.getMinute() * 60) + (localTime.getHour() * 60 * 60);
        return Duration.of(segundos, ChronoUnit.SECONDS);
    }
}