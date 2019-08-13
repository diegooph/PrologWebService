package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.IntegracaoException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;

/**
 * Created on 13/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RodoparHorizonteException extends IntegracaoException {
    public RodoparHorizonteException(@NotNull final String message, @Nullable final String developerMessage) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message, developerMessage);
    }

    public RodoparHorizonteException(@NotNull final String message,
                                     @Nullable final String developerMessage,
                                     @Nullable final Throwable exception) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message, developerMessage, exception);
    }
}
