package br.com.zalf.prolog.webservice.commons.util.date;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Created on 24/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class TimeRange {
    @NotNull
    private final LocalTime start;
    @NotNull
    private final LocalTime end;

    private TimeRange(@NotNull final LocalTime start, @NotNull final LocalTime end) {
        this.start = start;
        this.end = end;
    }

    @NotNull
    public static TimeRange of(final int startHour, final int endHour) {
        final LocalTime startTime = LocalTime.of(startHour % 24, 0);
        final LocalTime endTime = LocalTime.of(endHour % 24, 0);
        return new TimeRange(startTime, endTime);
    }

    @NotNull
    public static TimeRange of(@NotNull final LocalTime start, @NotNull final LocalTime end) {
        return new TimeRange(start, end);
    }

    @NotNull
    public static TimeRange of(@NotNull final LocalTime start, @NotNull final Duration length) {
        return new TimeRange(start, start.plus(length));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final TimeRange other = (TimeRange) obj;
        if (end == null) {
            if (other.end != null) {
                return false;
            }
        } else if (!end.equals(other.end)) {
            return false;
        }

        if (start == null) {
            return other.start == null;
        } else {
            return start.equals(other.start);
        }
    }

    @NotNull
    public LocalTime getStart() {
        return start;
    }

    @NotNull
    public LocalTime getEnd() {
        return end;
    }

    public boolean overlaps(@NotNull final TimeRange other) {
        if (startLaterThanEnd()) {
            return overlapsDayEnd(other);
        }
        if (other.startLaterThanEnd()) {
            return other.overlapsDayEnd(this);
        }
        assert start.isBefore(end) : "start '" + start + "' must be less than end '" + end + "'!";
        assert other.start.isBefore(other.end) : "other start must be less than other end!";
        return !start.isAfter(other.end) && !end.isBefore(other.start);
    }

    public boolean startLaterThanEnd() {
        return start.isAfter(end);
    }

    @NotNull
    public Duration getDuration() {
        final Duration duration = Duration.between(start, end);
        if (duration.isNegative()) {
            return duration.plusDays(1);
        }
        return duration;
    }

    public int toHours() {
        return (int) getDuration().toHours();
    }

    public int toMinutes() {
        return (int) getDuration().toMinutes();
    }

    private boolean overlapsDayEnd(@NotNull final TimeRange other) {
        final TimeRange firstPart = new TimeRange(start, LocalTime.MAX);
        final TimeRange secondPart = new TimeRange(LocalTime.MIN, end);
        return firstPart.overlaps(other) || secondPart.getDuration().isZero() || secondPart.overlaps(other);
    }
}