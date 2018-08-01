package br.com.zalf.prolog.webservice.errorhandling.sql;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.Response;

/**
 * Created on 18/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class DuplicateKeyException extends DataAccessException {

    public DuplicateKeyException(@NotNull final String message) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), ProLogErrorCodes.RECURSO_JA_EXISTE.errorCode(), message);
    }
}