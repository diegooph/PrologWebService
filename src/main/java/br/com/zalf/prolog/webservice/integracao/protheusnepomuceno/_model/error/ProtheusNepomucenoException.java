package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.IntegracaoException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;

/**
 * Created on 3/10/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class ProtheusNepomucenoException extends IntegracaoException {
    @NotNull
    private static final String ERROR_MESSAGE_DEFAULT = "[INTEGRAÇÃO] ";

    public ProtheusNepomucenoException(@NotNull final String message) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message);
    }

    public ProtheusNepomucenoException(final int httpStatusCode,
                                       @NotNull final String message) {
        super(httpStatusCode, message);
    }

    public ProtheusNepomucenoException(@NotNull final String message,
                                       @Nullable final String developerMessage,
                                       @Nullable final Throwable exception) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message, developerMessage, exception);
    }

    @NotNull
    public static String getPrettyMessage(@NotNull final String message) {
        return ERROR_MESSAGE_DEFAULT.concat(message);
    }
}
