package br.com.zalf.prolog.webservice.integracao.api;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.error.ProLogError;
import br.com.zalf.prolog.webservice.integracao.api.error.ApiGenericException;
import br.com.zalf.prolog.webservice.integracao.api.pneu.movimentacao.ApiProcessoMovimentacao;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created on 10/29/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaApiProLogRequesterImpl implements SistemaApiProLogRequester {
    @NotNull
    private static final String TAG = SistemaApiProLogRequesterImpl.class.getSimpleName();

    @NotNull
    @Override
    public SuccessResponseIntegracao insertProcessoMovimentacao(
            @NotNull final String url,
            @NotNull final String tokenIntegracao,
            @NotNull final ApiProcessoMovimentacao processoMovimentacao) throws Throwable {
        final SistemaApiProLogRest service = SistemaApiProLogRestClient.getService(SistemaApiProLogRest.class);
        final Call<SuccessResponseIntegracao> call = service.insertProcessoMovimentacao(url);
        return handleResponse(call.execute());
    }

    @NotNull
    private <T> T handleResponse(@Nullable final Response<T> response) throws Throwable {
        if (response != null) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                if (response.errorBody() == null) {
                    throw new ApiGenericException("[INTEGRAÇÃO] Erro ao movimentar pneus no sistema integrado");
                }
                throw ApiGenericException.from(toProLogError(response.errorBody()));
            }
        } else {
            throw new ApiGenericException("[INTEGRAÇÃO] Erro ao movimentar pneus no sistema integrado");
        }
    }

    @NotNull
    private ProLogError toProLogError(@NotNull final ResponseBody errorBody) throws Throwable {
        final String jsonBody = errorBody.string();
        try {
            return ProLogError.generateFromString(jsonBody);
        } catch (final Throwable t) {
            final String msg = String.format("Erro ao realizar o parse da mensagem de erro recebida da integração:\n" +
                    "jsonBody: %s", jsonBody);
            Log.e(TAG, msg, t);
            throw t;
        }
    }
}
