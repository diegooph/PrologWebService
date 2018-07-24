package br.com.zalf.prolog.webservice.commons.util.date;

import com.google.common.annotations.VisibleForTesting;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created on 24/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class DateTimeRange {

    private final ZonedDateTime from;
    private final ZonedDateTime to;

    private DateTimeRange(ZonedDateTime from, ZonedDateTime to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Returns a {@link DateTimeRange} which will beginn (inclusive) and end (exclusive) with the corresponding {@link ZonedDateTime}s of
     * {@code from} and {@code to} after the {@code zoneId} has been applied.
     *
     * @param from
     *            A {@link LocalDateTime} to which the {@code zoneId} will be applied. The resulting {@link ZonedDateTime} will be the
     *            beginning point in time of the {@link DateTimeRange} to create. This point in time is inclusive.
     * @param to
     *            A {@link LocalDateTime} to which the {@code zoneId} will be applied. The resulting {@link ZonedDateTime} will be the
     *            ending point in time of the {@link DateTimeRange} to create. This point in time is exclusive.
     * @param zoneId
     *            {@link ZonedDateTime} to be applied on {@code from} and {@code to}.
     * @return A {@link DateTimeRange}, not null.
     */
    public static DateTimeRange of(LocalDateTime from, LocalDateTime to, ZoneId zoneId) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Objects.requireNonNull(zoneId);

        return new DateTimeRange(ZonedDateTime.of(from, zoneId), ZonedDateTime.of(to, zoneId));
    }

    /**
     * Returns a {@link Stream} which processes this {@link DateTimeRange} with a resolution accroding to the {@code unit}.
     *
     * @param unit
     *            Unit to resolve this {@link DateTimeRange} for streaming.
     * @return A {@link Stream}, not null.
     */
    public Stream<ZonedDateTime> streamOn(ChronoUnit unit) {
        Objects.requireNonNull(unit);

        ZonedDateTimeSpliterator zonedDateTimeSpliterator = new ZonedDateTimeSpliterator(from, to, unit);
        return StreamSupport.stream(zonedDateTimeSpliterator, false);
    }

    @VisibleForTesting
    public static class ZonedDateTimeSpliterator implements Spliterator<ZonedDateTime> {

        private final ChronoUnit unit;

        private ZonedDateTime current;
        private ZonedDateTime to;

        @VisibleForTesting
        public ZonedDateTimeSpliterator(ZonedDateTime from, ZonedDateTime to, ChronoUnit unit) {
            this.current = from.truncatedTo(unit);
            this.to = to.truncatedTo(unit);
            this.unit = unit;
        }

        @Override
        public boolean tryAdvance(Consumer<? super ZonedDateTime> action) {
            boolean canAdvance = current.isBefore(to);

            if (canAdvance) {
                action.accept(current);
                current = current.plus(1, unit);
            }

            return canAdvance;
        }

        @Override
        public Spliterator<ZonedDateTime> trySplit() {
            long halfSize = estimateSize() / 2;
            if (halfSize == 0) {
                return null;
            }

            ZonedDateTime splittedFrom = current.plus(halfSize, unit);
            ZonedDateTime splittedTo = to;
            to = splittedFrom;

            return new ZonedDateTimeSpliterator(splittedFrom, splittedTo, unit);
        }

        @Override
        public long estimateSize() {
            return unit.between(current, to);
        }

        @Override
        public Comparator<? super ZonedDateTime> getComparator() {
            // sorted in natural order
            return null;
        }

        @Override
        public int characteristics() {
            return Spliterator.NONNULL | Spliterator.IMMUTABLE | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED | Spliterator.SORTED | Spliterator.DISTINCT;
        }

    }

}