package br.com.zalf.prolog.webservice.errorhandling.exception;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;


/**
 * Created on 07/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class NotAuthorizedException extends ProLogException {

    public NotAuthorizedException(@NotNull final String message) {
        super(Response.Status.UNAUTHORIZED.getStatusCode(),
              ProLogErrorCodes.NOT_AUTHORIZED.errorCode(),
              message);
    }

    public NotAuthorizedException(@NotNull final String message,
                                  @Nullable final String developerMessage) {
        super(Response.Status.UNAUTHORIZED.getStatusCode(),
                ProLogErrorCodes.NOT_AUTHORIZED.errorCode(),
                message,
                developerMessage);
    }
}
