package br.com.zalf.prolog.webservice.integracao.webfinatto._model.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.IntegracaoException;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.Response;

public class SistemaWebFinattoException extends IntegracaoException {
    public SistemaWebFinattoException(@NotNull final String message) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message);
    }

    public SistemaWebFinattoException(@NotNull final String message,
                                      @NotNull final String developerMessage,
                                      @NotNull final Throwable t) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message, developerMessage, t);
    }

    @NotNull
    public static SistemaWebFinattoException from(@NotNull final ResponseBody errorBody) {
        try {
            final String jsonErrorBody = errorBody.string();
            try {
                final ErrorResponseWebFinatto error = ErrorResponseWebFinatto.generateFromString(jsonErrorBody);
                return new SistemaWebFinattoException(error.getErrorMessage());
            } catch (final Exception e) {
                // Lançamos essa Exception para conseguirmos encapsular o JSON de erro que não foi convertido.
                // Só assim conseguiremos tratar de forma mais eficaz.
                throw new Exception("Erro ao realizar parse da mensagem de erro: " + jsonErrorBody, e);
            }
        } catch (final Throwable t) {
            throw new SistemaWebFinattoException(
                    "[INTEGRAÇÃO] Mensagem do sistema WebFinatto fora do padrão esperado",
                    "Não foi possível obter o JSON de resposta da requisição",
                    t);
        }
    }
}
