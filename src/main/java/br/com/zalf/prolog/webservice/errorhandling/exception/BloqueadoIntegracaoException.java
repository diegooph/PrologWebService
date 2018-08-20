package br.com.zalf.prolog.webservice.errorhandling.exception;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;

/**
 * Indica que este recurso foi bloqueado para um cliente por conta de uma integração que o ProLog possui com esse cliente
 * e que provavelmente não suporta o recurso sendo solicitado.
 *
 * Created on 20/08/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class BloqueadoIntegracaoException extends ProLogException {

    public BloqueadoIntegracaoException(@NotNull String message) {
        super(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), ProLogErrorCodes.INTEGRACAO.errorCode(), message, null);
    }

    public BloqueadoIntegracaoException(@NotNull String message, @Nullable String developerMessage) {
        super(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(),
                ProLogErrorCodes.INTEGRACAO.errorCode(), message, developerMessage);
    }
}