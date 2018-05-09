package br.com.zalf.prolog.webservice.errorhandling.exception;


import com.sun.istack.internal.Nullable;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.Response;

public class GenericException extends ProLogException {

    public GenericException(@NotNull final String message,
                            @Nullable final String developerMessage,
                            @NotNull final Exception parentException) {
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

}
