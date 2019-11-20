package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.IntegracaoException;
import br.com.zalf.prolog.webservice.integracao.praxio.data.GlobusPiccoloturMovimentacaoResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;

/**
 * Created on 17/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class GlobusPiccoloturException extends IntegracaoException {

    public GlobusPiccoloturException(@NotNull final String message) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message);
    }

    public GlobusPiccoloturException(@NotNull final String message,
                                     @Nullable final String developerMessage,
                                     @Nullable final Throwable exception) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message, developerMessage, exception);
    }

    @NotNull
    public static GlobusPiccoloturException from(@NotNull final GlobusPiccoloturMovimentacaoResponse response) {
        return new GlobusPiccoloturException(response.getPrettyErrors());
    }
}
