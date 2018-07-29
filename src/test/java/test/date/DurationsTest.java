package test.date;

/**
 * Created on 24/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */

public class DurationsTest {
//
//    @Test
//    public void testQuestion() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime from = ZonedDateTime.of(2018, 1, 1, 13, 0, 0, 0, london);
//        ZonedDateTime to = ZonedDateTime.of(2018, 1, 5, 4, 0, 0, 0, london);
//
//        LocalTime timeFrom = LocalTime.of(22, 0);
//        LocalTime timeTo = LocalTime.of(5, 0);
//
//        Duration hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(2, hoursOnFirstDay.toHours()); // 22 <-> 24
//
//        Duration hoursOnDay = getHoursOnDay(from.plusDays(1), timeFrom, timeTo);
//        assertEquals(7, hoursOnDay.toHours()); // 22 <-> 24 & 0 <-> 5
//        hoursOnDay = getHoursOnDay(from.plusDays(2), timeFrom, timeTo);
//        assertEquals(7, hoursOnDay.toHours()); // 22 <-> 24 & 0 <-> 5
//        hoursOnDay = getHoursOnDay(from.plusDays(3), timeFrom, timeTo);
//        assertEquals(7, hoursOnDay.toHours()); // 22 <-> 24 & 0 <-> 5
//
//        Duration hoursOnLastDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(4, hoursOnLastDay.toHours()); // 0 <-> 4
//
//        Duration sumOfHoursOnDays = getSumOfHoursOnDays(from, to, timeFrom, timeTo);
//        assertEquals(27, sumOfHoursOnDays.toHours());
//    }
//
//    @Test
//    public void testQuestion2() {
//        LocalDateTime from = LocalDateTime.of(2018, 1, 1, 13, 0, 0, 0);
//        LocalDateTime to = LocalDateTime.of(2018, 1, 5, 4, 30, 0, 0);
//
//        LocalTime timeFrom = LocalTime.of(22, 0);
//        LocalTime timeTo = LocalTime.of(5, 0);
//
//        Duration hoursOnFirstDay = Durations.getDurationOnDaysWithPrecision(ZoneId.systemDefault(), from, to, timeFrom, timeTo, ChronoUnit.MINUTES);
//        assertEquals(27, hoursOnFirstDay.toHours());
//        assertEquals(30, hoursOnFirstDay.toMinutes() % ChronoUnit.HOURS.getDuration().toMinutes()); // 1650 % 60
//    }
//
//    @Test
//    public void testGetSumOfHoursOnDays_OnOneDay() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime dateTimeFrom = ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, london);
//        ZonedDateTime dateTimeTo = ZonedDateTime.of(2018, 1, 1, 23, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(0, 0);
//        LocalTime timeTo = LocalTime.of(23, 0);
//        Duration sumOfHoursOnDays = getSumOfHoursOnDays(dateTimeFrom, dateTimeTo, timeFrom, timeTo);
//        assertEquals(23, sumOfHoursOnDays.toHours());
//
//        timeFrom = LocalTime.of(10, 0);
//        timeTo = LocalTime.of(20, 0);
//        sumOfHoursOnDays = getSumOfHoursOnDays(dateTimeFrom, dateTimeTo, timeFrom, timeTo);
//        assertEquals(10, sumOfHoursOnDays.toHours());
//
//        dateTimeFrom = ZonedDateTime.of(2018, 1, 1, 8, 0, 0, 0, london);
//        dateTimeTo = ZonedDateTime.of(2018, 1, 1, 22, 0, 0, 0, london);
//        timeFrom = LocalTime.of(10, 0);
//        timeTo = LocalTime.of(20, 0);
//        sumOfHoursOnDays = getSumOfHoursOnDays(dateTimeFrom, dateTimeTo, timeFrom, timeTo);
//        assertEquals(10, sumOfHoursOnDays.toHours());
//
//        timeFrom = LocalTime.of(4, 0);
//        timeTo = LocalTime.of(23, 0);
//        sumOfHoursOnDays = getSumOfHoursOnDays(dateTimeFrom, dateTimeTo, timeFrom, timeTo);
//        assertEquals(14, sumOfHoursOnDays.toHours());
//
//        timeFrom = LocalTime.of(10, 0);
//        timeTo = LocalTime.of(23, 0);
//        sumOfHoursOnDays = getSumOfHoursOnDays(dateTimeFrom, dateTimeTo, timeFrom, timeTo);
//        assertEquals(12, sumOfHoursOnDays.toHours());
//
//        timeFrom = LocalTime.of(4, 0);
//        timeTo = LocalTime.of(18, 0);
//        sumOfHoursOnDays = getSumOfHoursOnDays(dateTimeFrom, dateTimeTo, timeFrom, timeTo);
//        assertEquals(10, sumOfHoursOnDays.toHours());
//    }
//
//    @Test
//    public void testGetSumOfHoursOnDays_BetweenMonths() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime dateTimeFrom = ZonedDateTime.of(2018, 1, 30, 0, 0, 0, 0, london);
//        ZonedDateTime dateTimeTo = ZonedDateTime.of(2018, 2, 1, 23, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(0, 0);
//        LocalTime timeTo = LocalTime.of(23, 0);
//        Duration sumOfHoursOnDays = getSumOfHoursOnDays(dateTimeFrom, dateTimeTo, timeFrom, timeTo);
//        assertEquals(23 * 3, sumOfHoursOnDays.toHours());
//    }
//
//    @Test
//    public void testGetSumOfHoursOnDays_BetweenYears() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime dateTimeFrom = ZonedDateTime.of(2018, 12, 30, 0, 0, 0, 0, london);
//        ZonedDateTime dateTimeTo = ZonedDateTime.of(2019, 1, 1, 23, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(0, 0);
//        LocalTime timeTo = LocalTime.of(23, 0);
//        Duration sumOfHoursOnDays = getSumOfHoursOnDays(dateTimeFrom, dateTimeTo, timeFrom, timeTo);
//        assertEquals(23 * 3, sumOfHoursOnDays.toHours());
//    }
//
//    @Test
//    public void testGetSumOfHoursOnDays_LeapYear() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime dateTimeFrom = ZonedDateTime.of(2020, 2, 28, 0, 0, 0, 0, london);
//        ZonedDateTime dateTimeTo = ZonedDateTime.of(2020, 3, 1, 23, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(0, 0);
//        LocalTime timeTo = LocalTime.of(23, 0);
//        Duration sumOfHoursOnDays = getSumOfHoursOnDays(dateTimeFrom, dateTimeTo, timeFrom, timeTo);
//        assertEquals(23 * 3, sumOfHoursOnDays.toHours());
//    }
//
//    @Test
//    public void testGetSumOfHoursOnDaysFromAfterTo() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime dateTimeFrom = ZonedDateTime.of(2018, 12, 2, 0, 0, 0, 0, london);
//        ZonedDateTime dateTimeTo = ZonedDateTime.of(2018, 12, 1, 20, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(0, 0);
//        LocalTime timeTo = LocalTime.of(23, 0);
//        Duration sumOfHoursOnDays = getSumOfHoursOnDays(dateTimeFrom, dateTimeTo, timeFrom, timeTo);
//        assertEquals(0, sumOfHoursOnDays.toHours()); // no backward computation
//
//        timeFrom = LocalTime.of(12, 0);
//        timeTo = LocalTime.of(0, 0);
//        sumOfHoursOnDays = getSumOfHoursOnDays(dateTimeFrom, dateTimeTo, timeFrom, timeTo);
//        assertEquals(0, sumOfHoursOnDays.toHours()); // no backward computation
//    }
//
//    @Test
//    public void testGetSumOfHoursOnDaysDST() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        // DST -1
//        ZonedDateTime from = ZonedDateTime.of(2018, 3, 24, 0, 0, 0, 0, london);
//        ZonedDateTime to = ZonedDateTime.of(2018, 3, 26, 22, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(0, 0);
//        LocalTime timeTo = LocalTime.of(12, 0);
//        Duration sumOfHours = getSumOfHoursOnDays(from, to, timeFrom, timeTo);
//        assertEquals(35, sumOfHours.toHours()); // 0 <-> 12 & 0 <-> 3 & 4 <-> 12 & 0 <-> 12
//
//        // DST -1
//        from = ZonedDateTime.of(2018, 3, 24, 23, 0, 0, 0, london);
//        to = ZonedDateTime.of(2018, 3, 26, 22, 0, 0, 0, london);
//        timeFrom = LocalTime.of(0, 0);
//        timeTo = LocalTime.of(12, 0);
//        sumOfHours = getSumOfHoursOnDays(from, to, timeFrom, timeTo);
//        assertEquals(23, sumOfHours.toHours()); // no match & 0 <-> 3 & 4 <-> 12 & 0 <-> 12
//
//        // DST +1
//        from = ZonedDateTime.of(2018, 10, 27, 0, 0, 0, 0, london);
//        to = ZonedDateTime.of(2018, 10, 29, 22, 0, 0, 0, london);
//        timeFrom = LocalTime.of(0, 0);
//        timeTo = LocalTime.of(12, 0);
//        sumOfHours = getSumOfHoursOnDays(from, to, timeFrom, timeTo);
//        assertEquals(37, sumOfHours.toHours()); // 0 <-> 12 & 0 <-> 3 & 2 <-> 12 & 0 <-> 12
//
//        // DST +1
//        from = ZonedDateTime.of(2018, 10, 27, 0, 0, 0, 0, london);
//        to = ZonedDateTime.of(2018, 10, 29, 0, 0, 0, 0, london);
//        timeFrom = LocalTime.of(0, 0);
//        timeTo = LocalTime.of(12, 0);
//        sumOfHours = getSumOfHoursOnDays(from, to, timeFrom, timeTo);
//        assertEquals(25, sumOfHours.toHours()); // 0 <-> 12 & 0 <-> 3 & 2 <-> 12 & no match
//
//        // DST -1 / +1
//        from = ZonedDateTime.of(2018, 3, 24, 0, 0, 0, 0, london);
//        to = ZonedDateTime.of(2018, 10, 29, 0, 0, 0, 0, london);
//        sumOfHours = getSumOfHoursOnDays(from, to, timeFrom, timeTo);
//        assertEquals(2628, sumOfHours.toHours()); // sum is even
//    }
//
//    @Test
//    public void testGetHoursOnFirstDay_FromSameDayAsTo_DailyFromBeforeTo() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime from = ZonedDateTime.of(2018, 1, 1, 13, 0, 0, 0, london);
//        ZonedDateTime to = ZonedDateTime.of(2018, 1, 1, 22, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(0, 0);
//        LocalTime timeTo = LocalTime.of(23, 0);
//        Duration hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(9, hoursOnFirstDay.toHours()); // 13 <-> 22
//
//        timeFrom = LocalTime.of(0, 0);
//        timeTo = LocalTime.of(21, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(8, hoursOnFirstDay.toHours()); // 13 <-> 21
//
//        timeFrom = LocalTime.of(13, 0);
//        timeTo = LocalTime.of(21, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(8, hoursOnFirstDay.toHours()); // 13 <-> 21
//
//        timeFrom = LocalTime.of(14, 0);
//        timeTo = LocalTime.of(21, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(7, hoursOnFirstDay.toHours()); // 14 <-> 21
//
//        from = ZonedDateTime.of(2018, 1, 1, 13, 0, 0, 0, london);
//        to = ZonedDateTime.of(2018, 1, 1, 19, 0, 0, 0, london);
//        timeFrom = LocalTime.of(11, 0);
//        timeTo = LocalTime.of(21, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(6, hoursOnFirstDay.toHours()); // 13 <-> 19
//
//        timeFrom = LocalTime.of(14, 0);
//        timeTo = LocalTime.of(23, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(5, hoursOnFirstDay.toHours()); // 14 <-> 19
//
//        timeFrom = LocalTime.of(19, 0);
//        timeTo = LocalTime.of(23, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(0, hoursOnFirstDay.toHours()); // 19 <-> 19
//
//        timeFrom = LocalTime.of(21, 0);
//        timeTo = LocalTime.of(23, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(0, hoursOnFirstDay.toHours()); // no match
//    }
//
//    @Test
//    public void testGetHoursOnFirstDay_FromSameDayAsTo_DailyFromAfterTo() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime from = ZonedDateTime.of(2018, 1, 1, 1, 0, 0, 0, london);
//        ZonedDateTime to = ZonedDateTime.of(2018, 1, 1, 22, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(21, 0);
//        LocalTime timeTo = LocalTime.of(3, 0);
//        Duration hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(3, hoursOnFirstDay.toHours()); // 1 <-> 3 & 21 <-> 22
//
//        from = ZonedDateTime.of(2018, 1, 1, 3, 0, 0, 0, london);
//        to = ZonedDateTime.of(2018, 1, 1, 22, 0, 0, 0, london);
//        timeFrom = LocalTime.of(21, 0);
//        timeTo = LocalTime.of(3, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(1, hoursOnFirstDay.toHours()); // 21 <-> 22
//
//        timeFrom = LocalTime.of(22, 0);
//        timeTo = LocalTime.of(3, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(0, hoursOnFirstDay.toHours()); // no match
//
//        timeFrom = LocalTime.of(23, 0);
//        timeTo = LocalTime.of(0, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(0, hoursOnFirstDay.toHours()); // no match
//
//        timeFrom = LocalTime.of(1, 0);
//        timeTo = LocalTime.of(0, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(19, hoursOnFirstDay.toHours()); // 3 <-> 22
//    }
//
//    @Test
//    public void testGetHoursOnFirstDay_FromBeforeTo_DailyFromBeforeTo() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime from = ZonedDateTime.of(2018, 1, 1, 13, 0, 0, 0, london);
//        ZonedDateTime to = ZonedDateTime.of(2018, 1, 2, 22, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(0, 0);
//        LocalTime timeTo = LocalTime.of(21, 0);
//        Duration hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(8, hoursOnFirstDay.toHours()); // 13 <-> 21
//
//        timeFrom = LocalTime.of(0, 0);
//        timeTo = LocalTime.of(23, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(10, hoursOnFirstDay.toHours()); // 13 <-> 23
//
//        timeFrom = LocalTime.of(13, 0);
//        timeTo = LocalTime.of(21, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(8, hoursOnFirstDay.toHours()); // 13 <-> 21
//
//        timeFrom = LocalTime.of(14, 0);
//        timeTo = LocalTime.of(21, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(7, hoursOnFirstDay.toHours()); // 14 <-> 21
//
//        timeFrom = LocalTime.of(13, 0);
//        timeTo = LocalTime.of(23, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(10, hoursOnFirstDay.toHours()); // 13 <-> 23
//
//        timeFrom = LocalTime.of(14, 0);
//        timeTo = LocalTime.of(23, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(9, hoursOnFirstDay.toHours()); // 14 <-> 23
//
//        timeFrom = LocalTime.of(22, 0);
//        timeTo = LocalTime.of(23, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(1, hoursOnFirstDay.toHours()); // 22 <-> 23
//
//        from = ZonedDateTime.of(2018, 3, 24, 23, 0, 0, 0, london);
//        to = ZonedDateTime.of(2018, 3, 26, 22, 0, 0, 0, london);
//        timeFrom = LocalTime.of(0, 0);
//        timeTo = LocalTime.of(12, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(0, hoursOnFirstDay.toHours()); // no match
//    }
//
//    @Test
//    public void testGetHoursOnFirstDay_FromBeforeTo_DailyFromAfterTo() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime from = ZonedDateTime.of(2018, 1, 1, 13, 0, 0, 0, london);
//        ZonedDateTime to = ZonedDateTime.of(2018, 1, 2, 22, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(23, 0);
//        LocalTime timeTo = LocalTime.of(21, 0);
//        Duration hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(9, hoursOnFirstDay.toHours()); // 13 <-> 21 & 23 <-> 24
//
//        from = ZonedDateTime.of(2018, 1, 1, 3, 0, 0, 0, london);
//        to = ZonedDateTime.of(2018, 1, 2, 22, 0, 0, 0, london);
//        timeFrom = LocalTime.of(18, 0);
//        timeTo = LocalTime.of(2, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(6, hoursOnFirstDay.toHours()); // 18 <-> 24
//
//        timeTo = LocalTime.of(4, 0);
//        timeFrom = LocalTime.of(18, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(7, hoursOnFirstDay.toHours()); // 3 <-> 4 & 18 <-> 24
//
//        timeFrom = LocalTime.of(22, 0);
//        timeTo = LocalTime.of(3, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(2, hoursOnFirstDay.toHours()); // 22 <-> 24
//
//        timeFrom = LocalTime.of(23, 0);
//        timeTo = LocalTime.of(0, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(1, hoursOnFirstDay.toHours()); // 23 <-> 24
//
//        timeFrom = LocalTime.of(1, 0);
//        timeTo = LocalTime.of(0, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(21, hoursOnFirstDay.toHours()); // 3 <-> 24
//    }
//
//    @Test
//    public void testGetHoursOnFirstDayDST() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime from = ZonedDateTime.of(2018, 3, 24, 0, 0, 0, 0, london);
//        ZonedDateTime to = ZonedDateTime.of(2018, 3, 25, 22, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(0, 0);
//        LocalTime timeTo = LocalTime.of(12, 0);
//        Duration hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(12, hoursOnFirstDay.toHours()); // 0 <-> 12
//
//        from = ZonedDateTime.of(2018, 3, 25, 0, 0, 0, 0, london); // DST +1
//        to = ZonedDateTime.of(2018, 3, 26, 22, 0, 0, 0, london);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(11, hoursOnFirstDay.toHours()); // 0 <-> 3 & 4 <-> 12
//
//        from = ZonedDateTime.of(2018, 10, 27, 0, 0, 0, 0, london);
//        to = ZonedDateTime.of(2018, 10, 28, 22, 0, 0, 0, london);
//        timeFrom = LocalTime.of(0, 0);
//        timeTo = LocalTime.of(12, 0);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(12, hoursOnFirstDay.toHours()); // 0 <-> 12
//
//        from = ZonedDateTime.of(2018, 10, 28, 0, 0, 0, 0, london); // DST -1
//        to = ZonedDateTime.of(2018, 10, 29, 22, 0, 0, 0, london);
//        hoursOnFirstDay = getHoursOnFirstDay(from, to, timeFrom, timeTo);
//        assertEquals(13, hoursOnFirstDay.toHours()); // 0 <-> 3 & 2 <-> 12
//    }
//
//    @Test
//    public void testGetHoursOnLastDayDST() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime from = ZonedDateTime.of(2018, 3, 23, 0, 0, 0, 0, london);
//        ZonedDateTime to = ZonedDateTime.of(2018, 3, 24, 22, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(0, 0);
//        LocalTime timeTo = LocalTime.of(12, 0);
//        Duration hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(12, hoursOnFirstDay.toHours()); // 0 <-> 12
//
//        from = ZonedDateTime.of(2018, 3, 24, 0, 0, 0, 0, london); // DST +1
//        to = ZonedDateTime.of(2018, 3, 25, 22, 0, 0, 0, london);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(11, hoursOnFirstDay.toHours()); // 0 <-> 3 & 4 <-> 12
//
//        from = ZonedDateTime.of(2018, 10, 26, 0, 0, 0, 0, london);
//        to = ZonedDateTime.of(2018, 10, 27, 22, 0, 0, 0, london);
//        timeFrom = LocalTime.of(0, 0);
//        timeTo = LocalTime.of(12, 0);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(12, hoursOnFirstDay.toHours()); // 0 <-> 12
//
//        from = ZonedDateTime.of(2018, 10, 27, 0, 0, 0, 0, london); // DST -1
//        to = ZonedDateTime.of(2018, 10, 28, 22, 0, 0, 0, london);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(13, hoursOnFirstDay.toHours()); // 0 <-> 3 & 2 <-> 12
//    }
//
//    @Test
//    public void testGetHoursOnLastDay_FromBeforeTo_DailyFromBeforeTo() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime from = ZonedDateTime.of(2018, 1, 1, 13, 0, 0, 0, london);
//        ZonedDateTime to = ZonedDateTime.of(2018, 1, 2, 22, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(0, 0);
//        LocalTime timeTo = LocalTime.of(23, 0);
//        Duration hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(22, hoursOnFirstDay.toHours()); // 0 <-> 22
//
//        timeFrom = LocalTime.of(0, 0);
//        timeTo = LocalTime.of(22, 0);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(22, hoursOnFirstDay.toHours()); // 0 <-> 22
//
//        timeFrom = LocalTime.of(10, 0);
//        timeTo = LocalTime.of(21, 0);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(11, hoursOnFirstDay.toHours()); // 10 <-> 21
//
//        timeFrom = LocalTime.of(13, 0);
//        timeTo = LocalTime.of(23, 0);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(9, hoursOnFirstDay.toHours()); // 13 <-> 22
//
//        timeFrom = LocalTime.of(22, 0);
//        timeTo = LocalTime.of(23, 0);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(0, hoursOnFirstDay.toHours()); // 22 <-> 22
//
//        from = ZonedDateTime.of(2018, 1, 1, 13, 0, 0, 0, london);
//        to = ZonedDateTime.of(2018, 1, 2, 8, 0, 0, 0, london);
//        timeFrom = LocalTime.of(0, 0);
//        timeTo = LocalTime.of(23, 0);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(8, hoursOnFirstDay.toHours()); // 0 <-> 8
//
//        timeFrom = LocalTime.of(2, 0);
//        timeTo = LocalTime.of(6, 0);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(4, hoursOnFirstDay.toHours()); // 2 <-> 6
//
//        timeFrom = LocalTime.of(8, 0);
//        timeTo = LocalTime.of(23, 0);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(0, hoursOnFirstDay.toHours()); // no match
//    }
//
//    @Test
//    public void testGetHoursOnLastDay_FromBeforeTo_DailyFromAfterTo() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime from = ZonedDateTime.of(2018, 1, 1, 13, 0, 0, 0, london);
//        ZonedDateTime to = ZonedDateTime.of(2018, 1, 2, 22, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(23, 0);
//        LocalTime timeTo = LocalTime.of(21, 0);
//        Duration hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(21, hoursOnFirstDay.toHours()); // 0 <-> 21
//
//        from = ZonedDateTime.of(2018, 1, 1, 13, 0, 0, 0, london);
//        to = ZonedDateTime.of(2018, 1, 2, 20, 0, 0, 0, london);
//        timeFrom = LocalTime.of(23, 0);
//        timeTo = LocalTime.of(21, 0);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(20, hoursOnFirstDay.toHours()); // 0 <-> 20
//
//        timeTo = LocalTime.of(2, 0);
//        timeFrom = LocalTime.of(18, 0);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(4, hoursOnFirstDay.toHours()); // 0 <-> 2 & 18 <-> 20
//
//        from = ZonedDateTime.of(2018, 1, 1, 13, 0, 0, 0, london);
//        to = ZonedDateTime.of(2018, 1, 2, 16, 0, 0, 0, london);
//        timeTo = LocalTime.of(2, 0);
//        timeFrom = LocalTime.of(18, 0);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(2, hoursOnFirstDay.toHours()); // 0 <-> 2
//
//        timeFrom = LocalTime.of(15, 0);
//        timeTo = LocalTime.of(0, 0);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(1, hoursOnFirstDay.toHours()); // 15 <-> 16
//
//        timeFrom = LocalTime.of(16, 0);
//        timeTo = LocalTime.of(0, 0);
//        hoursOnFirstDay = getHoursOnLastDay(from, to, timeFrom, timeTo);
//        assertEquals(0, hoursOnFirstDay.toHours()); // no match
//    }
//
//    @Test
//    public void testGetHoursOnDay() {
//        ZoneId london = ZoneId.of("Europe/London");
//
//        ZonedDateTime day = ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, london);
//        LocalTime timeFrom = LocalTime.of(0, 0);
//        LocalTime timeTo = LocalTime.of(23, 0);
//        Duration hoursOnLastDay = getHoursOnDay(day, timeFrom, timeTo);
//        assertEquals(23, hoursOnLastDay.toHours());
//
//        day = ZonedDateTime.of(2018, 1, 1, 23, 0, 0, 0, london);
//        timeFrom = LocalTime.of(0, 0);
//        timeTo = LocalTime.of(23, 0);
//        hoursOnLastDay = getHoursOnDay(day, timeFrom, timeTo);
//        assertEquals(23, hoursOnLastDay.toHours());
//
//        day = ZonedDateTime.of(2018, 1, 1, 23, 0, 0, 0, london);
//        timeFrom = LocalTime.of(22, 0);
//        timeTo = LocalTime.of(23, 0);
//        hoursOnLastDay = getHoursOnDay(day, timeFrom, timeTo);
//        assertEquals(1, hoursOnLastDay.toHours());
//
//        timeFrom = LocalTime.of(22, 0);
//        timeTo = LocalTime.of(4, 0);
//        hoursOnLastDay = getHoursOnDay(day, timeFrom, timeTo);
//        assertEquals(6, hoursOnLastDay.toHours());
//
//        day = ZonedDateTime.of(2018, 3, 25, 23, 0, 0, 0, london); // DST +1
//        timeFrom = LocalTime.of(22, 0);
//        timeTo = LocalTime.of(4, 0);
//        hoursOnLastDay = getHoursOnDay(day, timeFrom, timeTo);
//        assertEquals(5, hoursOnLastDay.toHours());
//
//        day = ZonedDateTime.of(2018, 10, 28, 23, 0, 0, 0, london); // DST -11
//        timeFrom = LocalTime.of(22, 0);
//        timeTo = LocalTime.of(4, 0);
//        hoursOnLastDay = getHoursOnDay(day, timeFrom, timeTo);
//        assertEquals(7, hoursOnLastDay.toHours());
//    }
//
//    // following code is taken from previous solution and adopted to current approach to reuse tests
//
//    protected static Duration getSumOfHoursOnDays(ZonedDateTime dateTimeFrom, ZonedDateTime dateTimeTo, LocalTime dailyTimeFrom, LocalTime dailyTimeTo) {
//        ZoneId zone = dateTimeFrom.getZone();
//        return Durations.getSumOfHoursInRangeOnDays(zone, dateTimeFrom.toLocalDateTime(), dateTimeTo.toLocalDateTime(), TimeRange.of(dailyTimeFrom, dailyTimeTo));
//    }
//
//    protected static Duration getHoursOnFirstDay(ZonedDateTime dateTimeFrom, ZonedDateTime dateTimeTo, LocalTime dailyTimeFrom, LocalTime dailyTimeTo) {
//        ZoneId zone = dateTimeFrom.getZone();
//
//        LocalDateTime to = dateTimeFrom.truncatedTo(ChronoUnit.DAYS).isBefore(dateTimeTo.truncatedTo(ChronoUnit.DAYS)) ?
//                dateTimeFrom.plusDays(1).withHour(0).toLocalDateTime() :
//                dateTimeTo.toLocalDateTime();
//        DateTimeRange dtr = DateTimeRange.of(dateTimeFrom.toLocalDateTime(), to, zone);
//
//        long hours = dtr.streamOn(ChronoUnit.HOURS).filter(Durations.getFilter(dailyTimeFrom, dailyTimeTo)).count();
//        return Duration.ofHours(hours);
//    }
//
//    protected static Duration getHoursOnLastDay(ZonedDateTime dateTimeFrom, ZonedDateTime dateTimeTo, LocalTime dailyTimeFrom, LocalTime dailyTimeTo) {
//        ZoneId zone = dateTimeFrom.getZone();
//
//        LocalDateTime from = dateTimeFrom.truncatedTo(ChronoUnit.DAYS).isBefore(dateTimeTo.truncatedTo(ChronoUnit.DAYS)) ?
//                dateTimeTo.withHour(0).toLocalDateTime() :
//                dateTimeFrom.toLocalDateTime();
//        DateTimeRange dtr = DateTimeRange.of(from, dateTimeTo.toLocalDateTime(), zone);
//
//        long hours = dtr.streamOn(ChronoUnit.HOURS).filter(Durations.getFilter(dailyTimeFrom, dailyTimeTo)).count();
//        return Duration.ofHours(hours);
//    }
//
//    protected static Duration getHoursOnDay(ZonedDateTime day, LocalTime dailyTimeFrom, LocalTime dailyTimeTo) {
//        ZonedDateTime zoneTimeFrom = day.with(dailyTimeFrom);
//        ZonedDateTime zoneTimeTo = day.with(dailyTimeTo);
//        return zoneTimeFrom.isBefore(zoneTimeTo) ?
//                Duration.between(zoneTimeFrom, zoneTimeTo) :
//                Duration.between(day.withHour(0), zoneTimeTo).plus(Duration.between(zoneTimeFrom, day.plusDays(1).withHour(0)));
//    }
//
//    protected static Duration getHoursOnDay(ZonedDateTime dateTimeFrom, ZonedDateTime dateTimeTo, LocalTime dailyTimeFrom, LocalTime dailyTimeTo) {
//        ZoneId zone = dateTimeFrom.getZone();
//
//        DateTimeRange dtr = DateTimeRange.of(dateTimeFrom.toLocalDateTime(), dateTimeTo.toLocalDateTime(), zone);
//
//        long hours = dtr.streamOn(ChronoUnit.HOURS).filter(Durations.getFilter(dailyTimeFrom, dailyTimeTo)).count();
//        return Duration.ofHours(hours);
//    }
}