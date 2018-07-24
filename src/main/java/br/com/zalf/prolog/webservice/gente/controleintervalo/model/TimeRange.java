package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

/**
 * Created on 24/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */

import java.time.Duration;
import java.time.LocalTime;

/**
 * A TimeRange is the range between two times.
 */
public class TimeRange {
    private final LocalTime start;
    private final LocalTime end;

    public static TimeRange of(final int startHour, final int endHour) {
        final LocalTime startTime = LocalTime.of(startHour % 24, 0);
        final LocalTime endTime = LocalTime.of(endHour % 24, 0);
        return new TimeRange(startTime, endTime);
    }

    public TimeRange(final LocalTime start, final Duration length) {
        this(start, start.plus(length));
    }

    public TimeRange(final LocalTime start, final LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public boolean overlaps(final TimeRange other) {
        if (endsNextDay()) {
            return overlapsDayEnd(other);
        }
        if (other.endsNextDay()) {
            return other.overlapsDayEnd(this);
        }
        assert start.isBefore(end) : "start '" + start + "' must be less than end '" + end + "'!";
        assert other.start.isBefore(other.end) : "other start must be less than other end!";
        return !start.isAfter(other.end) && !end.isBefore(other.start);
    }

    private boolean overlapsDayEnd(final TimeRange other) {
        // check rest of this day and start of next day separately
        final TimeRange firstPart = new TimeRange(start, LocalTime.MAX);
        final TimeRange secondPart = new TimeRange(LocalTime.MIN, end);
        return firstPart.overlaps(other) || secondPart.getDuration().isZero() || secondPart.overlaps(other);
    }

    /**
     * @return <code>true</code> if the end of this time range is an the next day.
     */
    public boolean endsNextDay() {
        return start.isAfter(end);
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TimeRange other = (TimeRange) obj;
        if (end == null) {
            if (other.end != null)
                return false;
        } else if (!end.equals(other.end))
            return false;
        if (start == null) {
            return other.start == null;
        } else {
            return start.equals(other.start);
        }
    }
}