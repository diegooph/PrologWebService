package br.com.zalf.prolog.webservice.errorhandling.exception;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 11/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiariaException extends ProLogException {

    public EscalaDiariaException(@NotNull final String message,
                                 @NotNull final String developerMessage,
                                 @NotNull final Exception parentException) {
        super(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                ProLogErrorCodes.ESCALA_DIARIA.errorCode(),
                message,
                developerMessage,
                parentException);
    }
}