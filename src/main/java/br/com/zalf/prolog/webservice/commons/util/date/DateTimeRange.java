package br.com.zalf.prolog.webservice.commons.util.date;

import com.google.common.annotations.VisibleForTesting;

import java.time.LocalDateTime;
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

    private final LocalDateTime from;
    private final LocalDateTime to;

    private DateTimeRange(LocalDateTime from, LocalDateTime to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Returns a {@link DateTimeRange} which will beginn (inclusive) and end (exclusive) with the corresponding
     * {@link LocalDateTime}s of
     * {@code from} and {@code to}.
     *
     * @param from The beginning point in time of the {@link DateTimeRange} to create. This point in time is inclusive.
     * @param to   The ending point in time of the {@link DateTimeRange} to create. This point in time is exclusive.
     * @return A {@link DateTimeRange}, not null.
     */
    public static DateTimeRange of(LocalDateTime from, LocalDateTime to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);

        return new DateTimeRange(from, to);
    }

    /**
     * Returns a {@link Stream} which processes this {@link DateTimeRange} with a resolution accroding to the {@code
     * unit}.
     *
     * @param unit Unit to resolve this {@link DateTimeRange} for streaming.
     * @return A {@link Stream}, not null.
     */
    public Stream<LocalDateTime> streamOn(ChronoUnit unit) {
        Objects.requireNonNull(unit);

        LocalDateTimeSpliterator localDateTimeSpliterator = new LocalDateTimeSpliterator(from, to, unit);
        return StreamSupport.stream(localDateTimeSpliterator, false);
    }

    @VisibleForTesting
    public static class LocalDateTimeSpliterator implements Spliterator<LocalDateTime> {

        private final ChronoUnit unit;

        private LocalDateTime current;
        private LocalDateTime to;

        @VisibleForTesting
        public LocalDateTimeSpliterator(LocalDateTime from, LocalDateTime to, ChronoUnit unit) {
            this.current = from.truncatedTo(unit);
            this.to = to.truncatedTo(unit);
            this.unit = unit;
        }

        @Override
        public boolean tryAdvance(Consumer<? super LocalDateTime> action) {
            boolean canAdvance = current.isBefore(to);

            if (canAdvance) {
                action.accept(current);
                current = current.plus(1, unit);
            }

            return canAdvance;
        }

        @Override
        public Spliterator<LocalDateTime> trySplit() {
            long halfSize = estimateSize() / 2;
            if (halfSize == 0) {
                return null;
            }

            LocalDateTime splittedFrom = current.plus(halfSize, unit);
            LocalDateTime splittedTo = to;
            to = splittedFrom;

            return new LocalDateTimeSpliterator(splittedFrom, splittedTo, unit);
        }

        @Override
        public long estimateSize() {
            return unit.between(current, to);
        }

        @Override
        public Comparator<? super LocalDateTime> getComparator() {
            // Sorted in natural order.
            return null;
        }

        @Override
        public int characteristics() {
            return Spliterator.NONNULL | Spliterator.IMMUTABLE | Spliterator.SIZED | Spliterator.SUBSIZED |
                    Spliterator.ORDERED | Spliterator.SORTED | Spliterator.DISTINCT;
        }
    }
}