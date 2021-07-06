package br.com.zalf.prolog.webservice.errorhandling.exception;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 16/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class IntegracaoException extends ProLogException {

    public IntegracaoException(final int httpStatusCode, @NotNull final String message) {
        super(httpStatusCode, ProLogErrorCodes.INTEGRACAO.errorCode(), message);
    }

    public IntegracaoException(final int httpStatusCode,
                               @NotNull final String message,
                               @NotNull final String developerMessage) {
        super(httpStatusCode, ProLogErrorCodes.INTEGRACAO.errorCode(), message, developerMessage);
    }

    public IntegracaoException(final int httpStatusCode,
                               @NotNull final String message,
                               @Nullable final String developerMessage,
                               @Nullable final Throwable exception) {
        super(httpStatusCode, ProLogErrorCodes.INTEGRACAO.errorCode(), message, developerMessage, exception);
    }
}
