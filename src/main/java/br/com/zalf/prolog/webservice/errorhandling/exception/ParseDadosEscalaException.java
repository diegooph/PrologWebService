package br.com.zalf.prolog.webservice.errorhandling.exception;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 11/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ParseDadosEscalaException extends ProLogException {

    public ParseDadosEscalaException(final int httpStatusCode,
                                     @NotNull final String message,
                                     @NotNull final String developerMessage) {
        super(httpStatusCode, ProLogErrorCodes.PARSE_DADOS_ESCALA.errorCode(), message, developerMessage);
    }

    public ParseDadosEscalaException(final int httpStatusCode,
                                     @NotNull final String message,
                                     @NotNull final String developerMessage,
                                     @NotNull final Exception parentException) {
        super(httpStatusCode, ProLogErrorCodes.PARSE_DADOS_ESCALA.errorCode(), message, developerMessage, parentException);
    }
}
