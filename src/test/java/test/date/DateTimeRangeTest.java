package test.date;

import br.com.zalf.prolog.webservice.commons.util.date.DateTimeRange;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Spliterator;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created on 24/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class DateTimeRangeTest {

    @Test
    public void testRangeStreamedOnSamePointInTimeRegardingUnit() {
        LocalDateTime from = LocalDateTime.of(2018, 6, 1, 22, 12, 50, 12);
        LocalDateTime to = from.plus(10, ChronoUnit.MINUTES);
        Stream<ZonedDateTime> streamOn = DateTimeRange.of(from, to, ZoneId.systemDefault()).streamOn(ChronoUnit.HOURS);
        assertEquals(0, streamOn.count());
    }

    @Test
    public void testRangeStreamedOnMultiplePointsOfTime() {
        LocalDateTime from = LocalDateTime.of(2018, 6, 1, 22, 12, 50, 12);
        LocalDateTime to = from.plus(10, ChronoUnit.HOURS);
        Stream<ZonedDateTime> streamOn = DateTimeRange.of(from, to, ZoneId.systemDefault()).streamOn(ChronoUnit.HOURS);
        assertEquals(10, streamOn.count());
    }

    @Test
    public void testSpliteratorWithOneTimePointNanos() {
        LocalDateTime from = LocalDateTime.of(2018, 6, 1, 22, 12, 50, 12);
        LocalDateTime to = from;
        DateTimeRange.ZonedDateTimeSpliterator spliterator;
        spliterator = new DateTimeRange.ZonedDateTimeSpliterator(zonedDateTimeOf(from), zonedDateTimeOf(to), ChronoUnit.NANOS);
        assertEquals(0, spliterator.estimateSize());
        assertFalse(spliterator.tryAdvance(zdt -> fail("No element expected to advance to!")));
    }

    @Test
    public void testSpliteratorWithOneTimePointHours() {
        LocalDateTime from = LocalDateTime.of(2018, 6, 1, 22, 12, 50, 12);
        LocalDateTime to = from;
        DateTimeRange.ZonedDateTimeSpliterator spliterator = new DateTimeRange.ZonedDateTimeSpliterator(zonedDateTimeOf(from), zonedDateTimeOf(to), ChronoUnit.HOURS);
        assertEquals(0, spliterator.estimateSize());
        assertFalse(spliterator.tryAdvance(zdt -> fail("No element expected to advance to!")));
    }

    @Test
    public void testSpliteratorWithMultipleTimePoints() {
        LocalDateTime from = LocalDateTime.of(2018, 6, 1, 22, 12, 50, 12);
        LocalDateTime to = from.plus(10, ChronoUnit.NANOS);
        DateTimeRange.ZonedDateTimeSpliterator spliterator = new DateTimeRange.ZonedDateTimeSpliterator(zonedDateTimeOf(from), zonedDateTimeOf(to), ChronoUnit.NANOS);

        assertEquals(10, spliterator.estimateSize());
        assertTrue(spliterator.tryAdvance(zdt -> assertEquals(12, zdt.getNano())));
        assertEquals(9, spliterator.estimateSize());

        Spliterator<ZonedDateTime> secondSpliterator = spliterator.trySplit();
        assertEquals(4, spliterator.estimateSize());
        assertEquals(5, secondSpliterator.estimateSize());

        assertTrue(spliterator.tryAdvance(zdt -> assertEquals(13, zdt.getNano())));
        assertTrue(spliterator.tryAdvance(zdt -> assertEquals(14, zdt.getNano())));
        assertTrue(spliterator.tryAdvance(zdt -> assertEquals(15, zdt.getNano())));
        assertTrue(spliterator.tryAdvance(zdt -> assertEquals(16, zdt.getNano())));
        assertFalse(spliterator.tryAdvance(zdt -> fail("No element expected to advance to!")));

        Spliterator<ZonedDateTime> thirdSpliterator = secondSpliterator.trySplit();
        assertEquals(2, secondSpliterator.estimateSize());
        assertEquals(3, thirdSpliterator.estimateSize());
        Spliterator<ZonedDateTime> fourthSpliterator = thirdSpliterator.trySplit();
        assertEquals(1, thirdSpliterator.estimateSize());
        assertEquals(2, fourthSpliterator.estimateSize());

        assertNull(thirdSpliterator.trySplit());

        Spliterator<ZonedDateTime> fithSpliterator = fourthSpliterator.trySplit();
        assertEquals(1, fithSpliterator.estimateSize());
        assertNull(fithSpliterator.trySplit());

        assertTrue(secondSpliterator.tryAdvance(zdt -> assertEquals(17, zdt.getNano())));
        assertTrue(secondSpliterator.tryAdvance(zdt -> assertEquals(18, zdt.getNano())));
        assertFalse(secondSpliterator.tryAdvance(zdt -> fail("No element expected to advance to!")));

        assertTrue(thirdSpliterator.tryAdvance(zdt -> assertEquals(19, zdt.getNano())));
        assertFalse(thirdSpliterator.tryAdvance(zdt -> fail("No element expected to advance to!")));

        assertTrue(fourthSpliterator.tryAdvance(zdt -> assertEquals(20, zdt.getNano())));
        assertFalse(fourthSpliterator.tryAdvance(zdt -> fail("No element expected to advance to!")));

        assertTrue(fithSpliterator.tryAdvance(zdt -> assertEquals(21, zdt.getNano())));
        assertFalse(fithSpliterator.tryAdvance(zdt -> fail("No element expected to advance to!")));
    }

    protected ZonedDateTime zonedDateTimeOf(LocalDateTime from) {
        return ZonedDateTime.of(from.toLocalDate(), from.toLocalTime(), ZoneId.systemDefault());
    }

}
