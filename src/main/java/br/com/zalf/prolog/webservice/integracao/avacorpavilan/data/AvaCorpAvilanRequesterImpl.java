package br.com.zalf.prolog.webservice.integracao.avacorpavilan.data;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.InfosEnvioOsIntegracao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvaCorpAvilanException;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.os._model.OsAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.data.AvaCorpAvilanRest;
import br.com.zalf.prolog.webservice.integracao.network.RestClient;
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
    public void insertChecklistOs(@NotNull final InfosEnvioOsIntegracao infosEnvioOsIntegracao,
                                  @NotNull final OsAvilan osAvilan) throws Throwable {
        final AvaCorpAvilanRest service = RestClient.getService(AvaCorpAvilanRest.class);
        final Call<Void> call = service.insertChecklistOs(
                "Basic VXN1YXJpb0ludGVncmFjYW9Nb2JpbGU6VSRFUiFOVDNHUjRDNDA=",
                infosEnvioOsIntegracao.getUrlEnvio(),
                osAvilan);
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
