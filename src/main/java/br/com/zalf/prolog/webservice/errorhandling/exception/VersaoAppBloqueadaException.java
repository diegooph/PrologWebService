package br.com.zalf.prolog.webservice.errorhandling.exception;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.Response;

/**
 * Created on 13/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class VersaoAppBloqueadaException extends ProLogException {

    public VersaoAppBloqueadaException(@NotNull final String message) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
              ProLogErrorCodes.VERSAO_APP_BLOQUEADA.errorCode(),
              message);
    }
}