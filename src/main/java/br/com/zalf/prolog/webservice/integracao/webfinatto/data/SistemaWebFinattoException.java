package br.com.zalf.prolog.webservice.integracao.webfinatto.data;

import br.com.zalf.prolog.webservice.errorhandling.exception.IntegracaoException;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.Response;

public class SistemaWebFinattoException extends IntegracaoException {
    public SistemaWebFinattoException(@NotNull final String message) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message);
    }
}
