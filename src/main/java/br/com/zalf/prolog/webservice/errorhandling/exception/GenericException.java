package br.com.zalf.prolog.webservice.errorhandling.exception;


import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;

public class GenericException extends ProLogException {

    public GenericException(@NotNull final String message,
                            final int httpStatusCode,
                            @Nullable final String developerMessage,
                            @NotNull final Throwable parentException) {
        super(httpStatusCode,
                ProLogErrorCodes.GENERIC.errorCode(),
                message,
                developerMessage,
                parentException);
    }

    public GenericException(@NotNull final String message,
                            @Nullable final String developerMessage,
                            @NotNull final Throwable parentException) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                ProLogErrorCodes.GENERIC.errorCode(),
                message,
                developerMessage,
                parentException);
    }

    public GenericException(@NotNull final String message,
                            @Nullable final String developerMessage) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                ProLogErrorCodes.GENERIC.errorCode(),
                message,
                developerMessage);
    }

    public GenericException(@NotNull final String message) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                ProLogErrorCodes.GENERIC.errorCode(),
                message,
                null);
    }
}