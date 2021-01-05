package br.com.zalf.prolog.webservice.commons.util;


import br.com.zalf.prolog.webservice.commons.util.date.TimeUtils;

import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * Created 16/12/15
 *
 * @author Jean Zart (https://github.com/jeanzart)
 */
public class MetaUtils {
    private MetaUtils() {
        throw new IllegalStateException(MetaUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    public static boolean bateuMeta(final double resultado, final double meta) {
        return resultado <= meta;
    }

    public static boolean bateuMeta(final Time resultado, final Time meta) {
        final LocalTime r = TimeUtils.toLocalTime(resultado);
        final LocalTime m = TimeUtils.toLocalTime(meta);
        return r.isBefore(m) || r.equals(m);
    }

    public static boolean bateuMetaMapas(final double resultado, final double meta) {
        return resultado >= meta;
    }

    public static Time calculaTempoLargada(final Time hrSaida, final Time hrMatinal) {
        final LocalTime horaSaida = TimeUtils.toLocalTime(hrSaida);
        final LocalTime horaMatinal = TimeUtils.toLocalTime(hrMatinal);
        final LocalTime matinal = LocalTime.of(00, 30, 00);
        if (horaMatinal.isAfter(horaSaida)) {
            return TimeUtils.toSqlTime(matinal);
        } else {
            return TimeUtils.toSqlTime(horaSaida.minus(horaMatinal.getLong(ChronoField.MILLI_OF_DAY),
                                                       ChronoUnit.MILLIS));
        }
    }
}