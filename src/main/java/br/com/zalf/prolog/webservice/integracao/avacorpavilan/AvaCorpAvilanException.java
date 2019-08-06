package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.errorhandling.exception.IntegracaoException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;

/**
 * Created on 16/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class AvaCorpAvilanException extends IntegracaoException {

    public AvaCorpAvilanException(@NotNull final String message, @Nullable final String developerMessage) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message, developerMessage);
    }
}