package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;

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

    private AvaCorpAvilanUtils() {
        throw new IllegalStateException(AvaCorpAvilanUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    /**
     * Converte uma data para a representação em texto esperado no web service de integração da Avilan: yyyy-MM-dd.
     *
     * @param date um {@link Date}.
     * @return uma {@link String} represetando a data.
     */
    public static String createDatePattern(@NotNull final Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static int calculateDaysBetweenDateAndNow(@NotNull final String stringDate) {
        Preconditions.checkNotNull(stringDate, "stringDate não pode ser nula!");

        final DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd")
                .withLocale(Locale.getDefault());
        final LocalDate date = LocalDate.parse(stringDate, formatter);
        final LocalDate now = LocalDate.now();

        return (int) ChronoUnit.DAYS.between(date, now);
    }
}