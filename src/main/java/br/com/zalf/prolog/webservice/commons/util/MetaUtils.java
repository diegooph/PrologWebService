package br.com.zalf.prolog.webservice.commons.util;


import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * Created by jean on 16/12/15.
 * Utilitária para cáculo das metas e das flags de atingimento.
 */
public class MetaUtils {

    /**
     * Verifica se bateu a meta, para indicadores em que o resultado tem que ficar abaixo da meta (Ex Devolução)
     * @param resultado
     * @param meta
     * @return
     */
    public static boolean bateuMeta(double resultado, double meta){
        if(resultado <= meta) {
            return true;
        }
        return false;
    }

    // verifica indicadores com tempo (jornada, tempo interno, tempo em rota, largada...)

    /**
     * Verifica se bateu a meta, utilizado para indicadores de tempo (Ex Jornada)
     * @param resultado
     * @param meta
     * @return
     */
    public static boolean bateuMeta(Time resultado, Time meta) {
        LocalTime r = TimeUtils.toLocalTime(resultado);
        LocalTime m = TimeUtils.toLocalTime(meta);
        if(r.isBefore(m) || r.equals(m)) {
            return true;
        }
        return false;
    }

    /**
     * Verifica se bateu a meta, para indicadores em que o resultado tem que ser superior a meta (Ex Tracking)
     * @param resultado
     * @param meta
     * @return
     */
    public static boolean bateuMetaMapas(double resultado, double meta){
        if(resultado >= meta) {
            return true;
        }
        return false;
    }

    /**
     * Método utilitário para calcular o tempo de largada a partir do horário de saída e de matinal.
     * @param hrSaida
     * @param hrMatinal
     * @return
     */
    public static Time calculaTempoLargada(Time hrSaida, Time hrMatinal) {
        LocalTime horaSaida = TimeUtils.toLocalTime(hrSaida);
        LocalTime horaMatinal = TimeUtils.toLocalTime(hrMatinal);
        /**
         * Tempo fixo de matinal, 30 minutos.
         */
        LocalTime matinal = LocalTime.of(00, 30, 00);
        if(horaMatinal.isAfter(horaSaida)){
            return TimeUtils.toSqlTime(matinal);
        } else {
            return TimeUtils.toSqlTime(horaSaida.minus(horaMatinal.getLong(ChronoField.MILLI_OF_DAY), ChronoUnit.MILLIS));
        }
    }
}