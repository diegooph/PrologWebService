package br.com.zalf.prolog.webservice.errorhandling.sql;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.Response;

/**
 * Created on 2020-11-09
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class NotFoundException extends DataAccessException {
    public NotFoundException(@NotNull final String message) {
        super(Response.Status.ACCEPTED.getStatusCode(),
              ProLogErrorCodes.CHAVE_NAO_EXISTENTE.errorCode(),
              message);
    }

    public NotFoundException(@NotNull final String message, @NotNull final String detailedMessage) {
        super(Response.Status.ACCEPTED.getStatusCode(),
              ProLogErrorCodes.CHAVE_NAO_EXISTENTE.errorCode(),
              message,
              detailedMessage);
    }

    public NotFoundException(@NotNull final String message,
                             @NotNull final String detailedMessage,
                             final boolean loggableOnErrorReportSystem) {
        super(Response.Status.ACCEPTED.getStatusCode(),
              ProLogErrorCodes.CHAVE_NAO_EXISTENTE.errorCode(),
              message,
              detailedMessage,
              loggableOnErrorReportSystem);
    }

    public NotFoundException(@NotNull final String message,
                             @NotNull final String detailedMessage,
                             @NotNull final String developerMessage) {
        super(Response.Status.ACCEPTED.getStatusCode(),
              ProLogErrorCodes.CHAVE_NAO_EXISTENTE.errorCode(),
              message,
              detailedMessage,
              developerMessage,
              false);
    }

    @NotNull
    public static NotFoundException defaultNotLoggableException() {
        return new NotFoundException("Registro não encontrado.",
                                     "Chave de acesso ao registro não consta na base de dados.",
                                     false);
    }
}
