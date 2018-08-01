package br.com.zalf.prolog.webservice.errorhandling.exception;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;

/**
 * Created on 16/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RecapadoraException extends ProLogException {

    public RecapadoraException(final String message, final String developerMessage) {
        super(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                ProLogErrorCodes.RECAPADORA_EXCEPTION.errorCode(),
                message,
                developerMessage);
    }
}
