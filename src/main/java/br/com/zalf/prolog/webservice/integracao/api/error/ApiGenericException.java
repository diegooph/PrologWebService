package br.com.zalf.prolog.webservice.integracao.api.error;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogError;
import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.Response;

/**
 * Created on 04/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiGenericException extends ProLogException {
    public ApiGenericException(@NotNull final String msg) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), ProLogErrorCodes.INTEGRACAO.errorCode(), msg);
    }

    @NotNull
    public static ApiGenericException from(@NotNull final ProLogError proLogError) {
        return new ApiGenericException(proLogError.getMessage());
    }
}
