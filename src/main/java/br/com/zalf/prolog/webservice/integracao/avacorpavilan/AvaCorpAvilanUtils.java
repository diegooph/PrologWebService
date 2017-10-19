package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

/**
 * Created by luiz on 7/24/17.
 */
@VisibleForTesting
public class AvaCorpAvilanUtils {

    public static final int AVILAN_DATE_PATTERN_STRING_SIZE = 10;
    private static final SimpleDateFormat AVILAN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat AVILAN_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private AvaCorpAvilanUtils() {
        throw new IllegalStateException(AvaCorpAvilanUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    /**
     * Converte uma data para a representação em texto esperado no web service de integração da Avilan: yyyy-MM-dd.
     *
     * @param date um {@link Date}.
     * @return uma {@link String} represetando a data.
     */
    @NotNull
    public static String createDatePattern(@NotNull final Date date) {
        return AVILAN_DATE_FORMAT.format(date);
    }

    /**
     * Converte uma representação em texto no padrão utilizado no web service de integração da Avilan para um
     * {@link Date date}.
     *
     * @param dateString {@link String} representação da data.
     * @return um {@link Date}.
     */
    @NotNull
    public static Date createDatePattern(@NotNull final String dateString) throws ParseException {
        return AVILAN_DATE_FORMAT.parse(dateString);
    }

    @NotNull
    public static Date createDateTimePattern(@NotNull final String dateTimeString) throws ParseException {
        return AVILAN_DATE_TIME_FORMAT.parse(dateTimeString);
    }

    public static int calculateDaysBetweenDateAndNow(@NotNull final String stringDate) {
        Preconditions.checkNotNull(stringDate, "stringDate não pode ser nula!");

        final DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd/MM/yyyy hh:mm:ss")
                .withLocale(Locale.getDefault());
        final LocalDate date = LocalDate.parse(stringDate, formatter);
        final LocalDate now = LocalDate.now();

        return (int) ChronoUnit.DAYS.between(date, now);
    }
}