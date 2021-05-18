package br.com.zalf.prolog.webservice.errorhandling.sql;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;

/**
 * Created on 19/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
class DataAccessException extends ProLogException {

    DataAccessException(@NotNull final String message,
                        @Nullable final String developerMessage,
                        @NotNull final Throwable parentException) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
              ProLogErrorCodes.GENERIC.errorCode(),
              message,
              developerMessage,
              parentException);
    }

    DataAccessException(@NotNull final String message) {
        this(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), ProLogErrorCodes.GENERIC.errorCode(), message);
    }

    DataAccessException(final int httpStatusCode, final int proLogErrorCode, @NotNull final String message) {
        super(httpStatusCode, proLogErrorCode, message);
    }

    DataAccessException(final int httpStatusCode,
                        final int proLogErrorCode,
                        @NotNull final String message,
                        @NotNull final String detailedMessage) {
        super(httpStatusCode, proLogErrorCode, message, detailedMessage);
    }

    public DataAccessException(final int httpStatusCode,
                               final int proLogErrorCode,
                               @NotNull final String message,
                               @NotNull final String detailedMessage,
                               final boolean loggableOnErrorReportSystem) {
        super(httpStatusCode, proLogErrorCode, message, detailedMessage, loggableOnErrorReportSystem);
    }

    DataAccessException(final int httpStatusCode,
                        final int proLogErrorCode,
                        @NotNull final String message,
                        @NotNull final String detailedMessage,
                        @NotNull final String developerMessage,
                        final boolean loggableOnErrorReportSystem) {
        super(httpStatusCode, proLogErrorCode, message, detailedMessage, developerMessage, loggableOnErrorReportSystem);
    }
}