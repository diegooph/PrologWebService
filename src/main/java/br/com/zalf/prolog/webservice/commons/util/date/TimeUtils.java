package br.com.zalf.prolog.webservice.commons.util.date;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * Created by jean on 08/12/15.
 */
public class TimeUtils {

    /**
     * Faz a diferença entre duas horas (contando os minutos, não somente a hora) de dois objetos
     * java.sql.Time
     *
     * @param hora1 um Time
     * @param hora2 um Time
     * @return java.sql.Time
     */
    public static Time differenceBetween(Time hora1, Time hora2) {
        LocalTime h1 = TimeUtils.toLocalTime(hora1);
        LocalTime h2 = TimeUtils.toLocalTime(hora2);
        return TimeUtils.toSqlTime(h1.minus(h2.getLong(ChronoField.MILLI_OF_DAY), ChronoUnit.MILLIS));
    }

    /**
     * Soma as horas (contando os minutos, não somente a hora) de dois objetos java.sql.Time
     *
     * @param hora1 um Time
     * @param hora2 um Time
     * @return java.sql.Time
     */
    public static Time somaHoras(Time hora1, Time hora2) {
        LocalTime h1 = TimeUtils.toLocalTime(hora1);
        LocalTime h2 = TimeUtils.toLocalTime(hora2);
        return TimeUtils.toSqlTime(h1.plus(h2.getLong(ChronoField.MILLI_OF_DAY), ChronoUnit.MILLIS));
    }

    /**
     * Converte um java.sql.Time para java.time.LocalTime
     *
     * @param sqlTime um Time
     * @return java.time.LocalTime
     */
    public static LocalTime toLocalTime(Time sqlTime) {
        return sqlTime.toLocalTime();
    }

    /**
     * Converte um java.sql.Timestamp para java.time.LocalTime
     *
     * @param timeStamp um Timestamp
     * @return java.time.LocalTime
     */
    public static LocalTime toLocalTime(Timestamp timeStamp) {
        return timeStamp.toLocalDateTime().toLocalTime();
    }

    /**
     * Converte um java.time.LocalTime para java.sql.Time
     *
     * @param localTime um LocalTime
     * @return java.sql.Time
     */
    public static Time toSqlTime(LocalTime localTime) {
        return Time.valueOf(localTime);
    }

    /**
     * Converte um java.sql.Timestamp para java.sql.Time
     *
     * @param timestamp um Timestamp
     * @return java.sql.Time
     */
    public static Time toSqlTime(Timestamp timestamp) {
        return new Time(timestamp.getTime());
    }

    public static Time toSqlTime(String stringToParse) {
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