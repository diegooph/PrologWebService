package br.com.zalf.prolog.webservice.errorhandling.exception;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.Response;

/**
 * Created on 2020-10-15
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class BadRequestException extends ProLogException {
    public BadRequestException(@NotNull final String message) {
        super(Response.Status.BAD_REQUEST.getStatusCode(), ProLogErrorCodes.BAD_REQUEST.errorCode(), message);
    }

    public BadRequestException(final int httpStatusCode, final int proLogErrorCode, @NotNull final String message) {
        super(httpStatusCode, proLogErrorCode, message);
    }

    public BadRequestException(final int httpStatusCode,
                               final int proLogErrorCode,
                               @NotNull final String message,
                               @NotNull final String detailedMessage) {
        super(httpStatusCode, proLogErrorCode, message, detailedMessage);
    }

    public BadRequestException(@NotNull final String message, @NotNull final String developerMessage) {
        super(Response.Status.BAD_REQUEST.getStatusCode(),
                ProLogErrorCodes.BAD_REQUEST.errorCode(),
                message,
                developerMessage);
    }

    public BadRequestException(@NotNull final String message,
                               @NotNull final String detailedMessage,
                               @NotNull final String developerMessage,
                               final boolean loggableOnErrorReportSystem) {
        super(Response.Status.BAD_REQUEST.getStatusCode(),
                ProLogErrorCodes.BAD_REQUEST.errorCode(),
                message,
                detailedMessage,
                developerMessage,
                loggableOnErrorReportSystem);
    }

    public BadRequestException(final int httpStatusCode,
                               final int proLogErrorCode,
                               @NotNull final String message,
                               @NotNull final String detailedMessage,
                               @NotNull final String developerMessage,
                               final boolean loggableOnErrorReportSystem) {
        super(httpStatusCode, proLogErrorCode, message, detailedMessage, developerMessage, loggableOnErrorReportSystem);
    }
}
