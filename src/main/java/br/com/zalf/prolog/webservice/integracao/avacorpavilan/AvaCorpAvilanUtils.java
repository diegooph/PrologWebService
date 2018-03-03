package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Created by luiz on 7/24/17.
 */
@VisibleForTesting
public class AvaCorpAvilanUtils {

    public static final int AVILAN_DATE_PATTERN_STRING_SIZE = 10;
    private static final DateTimeFormatter AVILAN_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter AVILAN_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private AvaCorpAvilanUtils() {
        throw new IllegalStateException(AvaCorpAvilanUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    /**
     * Converte uma data para a representação em texto esperado no web service de integração da Avilan: yyyy-MM-dd.
     *
     * @param date um {@link LocalDate}.
     * @return uma {@link String} represetando a data.
     */
    @NotNull
    public static String createDatePattern(@NotNull final LocalDate date) {
        return AVILAN_DATE_FORMAT.format(date);
    }

    /**
     * Converte uma representação em texto no padrão utilizado no web service de integração da Avilan para um
     * {@link LocalDate date}.
     *
     * @param dateString {@link String} representação da data.
     * @return um {@link LocalDate}.
     */
    @NotNull
    public static LocalDate createDatePattern(@NotNull final String dateString) throws ParseException {
        return LocalDate.parse(dateString, AVILAN_DATE_FORMAT);
    }

    @NotNull
    public static String createDateTimePattern(@NotNull final LocalDateTime date) throws ParseException {
        return AVILAN_DATE_TIME_FORMAT.format(date);
    }

    @NotNull
    public static LocalDateTime createDateTimePattern(@NotNull final String dateTimeString) throws ParseException {
        return LocalDateTime.parse(dateTimeString, AVILAN_DATE_TIME_FORMAT);
    }

    public static int calculateDaysBetweenDateAndNow(@NotNull final String stringDate) {
        Preconditions.checkNotNull(stringDate, "stringDate não pode ser nula!");

        // TODO: TIMEZONE
        final DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd/MM/yyyy hh:mm:ss")
                .withLocale(Locale.getDefault());
        final LocalDate date = LocalDate.parse(stringDate, formatter);
        final LocalDate now = LocalDate.now();

        return (int) ChronoUnit.DAYS.between(date, now);
    }
}