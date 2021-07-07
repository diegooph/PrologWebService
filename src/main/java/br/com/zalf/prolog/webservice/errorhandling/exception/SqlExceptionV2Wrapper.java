package br.com.zalf.prolog.webservice.errorhandling.exception;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.Response;

/**
 * Created on 2021-07-02
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class SqlExceptionV2Wrapper extends ProLogException {
    public SqlExceptionV2Wrapper(@NotNull final Throwable parentException, @NotNull final String message) {
        super(Response.Status.BAD_REQUEST.getStatusCode(),
              ProLogErrorCodes.BAD_REQUEST.errorCode(),
              message,
              parentException);
    }
}
