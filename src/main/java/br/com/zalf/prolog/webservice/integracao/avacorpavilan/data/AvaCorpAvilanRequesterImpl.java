package br.com.zalf.prolog.webservice.integracao.avacorpavilan.data;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.OrdemServicoAvaCorpAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.error.AvaCorpAvilanException;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.error.ErrorResponseAvaCorpAvilan;
import br.com.zalf.prolog.webservice.integracao.network.RestClient;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created on 2020-08-31
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AvaCorpAvilanRequesterImpl implements AvaCorpAvilanRequester {
    @Override
    public void insertChecklistOs(@NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
                                  @NotNull final OrdemServicoAvaCorpAvilan ordemServicoAvaCorpAvilan) throws Throwable {
        if (apiAutenticacaoHolder.getApiTokenClient() == null) {
            throw new IllegalArgumentException("apiTokenClient não pode ser nulo.");
        }
        final AvaCorpAvilanRest service = RestClient.getService(AvaCorpAvilanRest.class);
        final Call<Object> call = service.insertChecklistOs(
                apiAutenticacaoHolder.getPrologTokenIntegracao(),
                apiAutenticacaoHolder.getApiTokenClient(),
                apiAutenticacaoHolder.getUrl(),
                ordemServicoAvaCorpAvilan);
        handleResponse(call.execute());
    }

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    private <T> T handleResponse(final Response<T> response) {
        if (response != null) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                if (response.errorBody() == null) {
                    throw new AvaCorpAvilanException(
                            "[INTEGRAÇÃO] Nenhuma resposta obtida do sistema AvaCorpAvilan");
                }
                if (response.code() == javax.ws.rs.core.Response.Status.UNAUTHORIZED.getStatusCode()) {
                    throw new AvaCorpAvilanException("[INTEGRAÇÃO] Token não autorizado");
                }
                final ErrorResponseAvaCorpAvilan error = toAvaCorpAvilanError(response.errorBody());
                throw new AvaCorpAvilanException(error.getMessage());
            }
        } else {
            throw new AvaCorpAvilanException(
                    "[INTEGRAÇÃO] Nenhuma resposta obtida do sistema AvaCorpAvilan",
                    "Um erro ocorreu ao realizar o request.");
        }
    }

    @NotNull
    private ErrorResponseAvaCorpAvilan toAvaCorpAvilanError(@NotNull final ResponseBody errorBody) {
        try {
            final String jsonErrorBody = errorBody.string();
            try {
                return ErrorResponseAvaCorpAvilan.generateFromString(jsonErrorBody);
            } catch (final Exception e) {
                // Lançamos essa Exception para conseguirmos encapsular o JSON de erro que não foi convertido.
                // Só assim conseguiremos tratar de forma mais eficaz.
                throw new Exception("Erro ao realizar parse da mensagem de erro: " + jsonErrorBody, e);
            }
        } catch (final Throwable t) {
            throw new AvaCorpAvilanException(
                    "[INTEGRAÇÃO] Mensagem do sistema AvaCorpAvilan fora do padrão esperado",
                    t);
        }
    }
}
