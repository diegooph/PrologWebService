package br.com.zalf.prolog.webservice.commons.util.date;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TimeRange;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.NotNull;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.function.Predicate;

/**
 * Created on 24/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class Durations {

    @NotNull
    public static String formatDuration(final long millis, @NotNull final String format) {
        return DurationFormatUtils.formatDuration(millis, format);
    }

    @NotNull
    public static String formatDuration(@NotNull final Duration duration, @NotNull final String format) {
        return formatDuration(duration.toMillis(), format);
    }

    public static Duration getSumOfHoursInRangeOnDays(LocalDateTime dateTimeFrom,
                                                      LocalDateTime dateTimeTo,
                                                      TimeRange timeRange) {
        return getDurationOnDaysWithPrecision(dateTimeFrom, dateTimeTo, timeRange.getStart(), timeRange.getEnd(), ChronoUnit.HOURS);
    }

    public static Duration getSumOfMinutesInRangeOnDays(LocalDateTime dateTimeFrom,
                                                        LocalDateTime dateTimeTo,
                                                        TimeRange timeRange) {
        return getDurationOnDaysWithPrecision(dateTimeFrom, dateTimeTo, timeRange.getStart(), timeRange.getEnd(), ChronoUnit.MINUTES);
    }

    public static Duration getDurationOnDaysWithPrecision(LocalDateTime dateTimeFrom,
                                                          LocalDateTime dateTimeTo,
                                                          TimeRange timeRange,
                                                          ChronoUnit precision) {
        final long count = DateTimeRange.of(dateTimeFrom, dateTimeTo)
                .streamOn(precision)
                .filter(getFilter(timeRange.getStart(), timeRange.getEnd()))
                .count();
        return Duration.of(count, precision);
    }

    public static Duration getDurationOnDaysWithPrecision(LocalDateTime dateTimeFrom,
                                                          LocalDateTime dateTimeTo,
                                                          LocalTime dailyTimeFrom,
                                                          LocalTime dailyTimeTo,
                                                          ChronoUnit precision) {
        final long count = DateTimeRange.of(dateTimeFrom, dateTimeTo)
                .streamOn(precision)
                .filter(getFilter(dailyTimeFrom, dailyTimeTo))
                .count();
        return Duration.of(count, precision);
    }

    @VisibleForTesting
    public static Predicate<? super LocalDateTime> getFilter(LocalTime dailyTimeFrom, LocalTime dailyTimeTo) {
        return dailyTimeFrom.isBefore(dailyTimeTo) ?
                filterFromTo(dailyTimeFrom, dailyTimeTo) :
                filterToFrom(dailyTimeFrom, dailyTimeTo);
    }

    private static Predicate<? super LocalDateTime> filterFromTo(LocalTime dailyTimeFrom, LocalTime dailyTimeTo) {
        return zdt -> {
            LocalTime time = zdt.toLocalTime();
            return (time.equals(dailyTimeFrom) || time.isAfter(dailyTimeFrom)) && time.isBefore(dailyTimeTo);
        };
    }

    private static Predicate<? super LocalDateTime> filterToFrom(LocalTime dailyTimeFrom, LocalTime dailyTimeTo) {
        return zdt -> {
            LocalTime time = zdt.toLocalTime();
            return (time.equals(dailyTimeFrom) || time.isAfter(dailyTimeFrom)) || (time.isBefore(dailyTimeTo));
        };
    }
}