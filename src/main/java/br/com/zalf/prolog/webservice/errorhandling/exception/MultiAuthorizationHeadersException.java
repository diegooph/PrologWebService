package br.com.zalf.prolog.webservice.errorhandling.exception;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;

/**
 * Created on 16/10/2020
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class MultiAuthorizationHeadersException extends ProLogException {
    public MultiAuthorizationHeadersException(@NotNull final String message) {
        super(Response.Status.UNAUTHORIZED.getStatusCode(),
                ProLogErrorCodes.MULTIPLES_AUTHORIZATIONS_HEADERS.errorCode(),
                message,
                null);
    }

    public MultiAuthorizationHeadersException(@NotNull final String message,
                                              @Nullable final String developerMessage) {
        super(Response.Status.UNAUTHORIZED.getStatusCode(),
                ProLogErrorCodes.MULTIPLES_AUTHORIZATIONS_HEADERS.errorCode(),
                message,
                developerMessage);
    }
}
