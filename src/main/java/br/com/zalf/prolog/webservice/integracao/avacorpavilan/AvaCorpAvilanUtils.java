package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import com.sun.istack.internal.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by luiz on 7/24/17.
 */
class AvaCorpAvilanUtils {

    private AvaCorpAvilanUtils() {
        throw new IllegalStateException(AvaCorpAvilanUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    /**
     * Converte uma data para a representação em texto esperado no web service de integração da Avilan: yyyy-MM-dd.
     *
     * @param date um {@link Date}.
     * @return uma {@link String} represetando a data.
     */
    static String createDatePattern(@NotNull final Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}