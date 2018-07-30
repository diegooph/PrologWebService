package br.com.zalf.prolog.webservice.commons.util.date;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.threeten.extra.Interval;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @NotNull
    public static Duration getSumOfHoursInRangeOnDays(@NotNull final LocalDateTime dateTimeFrom,
                                                      @NotNull final LocalDateTime dateTimeTo,
                                                      @NotNull final TimeRange timeRange,
                                                      @NotNull final ZoneId zoneId) {
        final ZonedDateTime zdtStart = dateTimeFrom.atZone(zoneId);
        final ZonedDateTime zdtStop = dateTimeTo.atZone(zoneId);

        final Interval interval = Interval.of(zdtStart.toInstant(), zdtStop.toInstant());

        final LocalDate ldStart = zdtStart.toLocalDate();
        final LocalDate ldStop = zdtStop.toLocalDate();
        LocalDate localDate = ldStart;

        final LocalTime timeStart = timeRange.getStart();
        final LocalTime timeStop = timeRange.getEnd();

        final long initialCapacity = (ChronoUnit.DAYS.between(ldStart, dateTimeTo) + 1);
        final Map<LocalDate, Interval> dateToIntervalMap = new HashMap<>((int) initialCapacity);
        while (!localDate.isAfter(ldStop)) {
            final ZonedDateTime zdtTargetStart = localDate.atTime(timeStart).atZone(zoneId);
            final ZonedDateTime zdtTargetStop = localDate.plusDays(1).atTime(timeStop).atZone(zoneId);
            final Interval target = Interval.of(zdtTargetStart.toInstant(), zdtTargetStop.toInstant());
            final Interval intersection;
            if (interval.overlaps(target)) {
                intersection = interval.intersection(target);
            } else {
                final ZonedDateTime emptyInterval = localDate.atTime(timeStart).atZone(zoneId);   // Better than NULL I suppose.
                intersection = Interval.of(emptyInterval.toInstant(), emptyInterval.toInstant());
            }
            dateToIntervalMap.put(localDate, intersection);
            // Setup the next loop.
            localDate = localDate.plusDays(1);
        }

        Duration totalDuration = Duration.ZERO;
        final List<LocalDate> dates = new ArrayList<>(dateToIntervalMap.keySet());
        for (final LocalDate date : dates) {
            totalDuration = totalDuration.plus(dateToIntervalMap.get(date).toDuration());
        }
        return totalDuration;
    }
}