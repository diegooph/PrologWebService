package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.IntegracaoException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;

/**
 * Created on 08/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ProtheusRodalogException extends IntegracaoException {
    public ProtheusRodalogException(@NotNull final String message, @Nullable final String developerMessage) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message, developerMessage);
    }

    public ProtheusRodalogException(@NotNull final String message,
                                    @Nullable final String developerMessage,
                                    @Nullable final Throwable exception) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message, developerMessage, exception);
    }
}
