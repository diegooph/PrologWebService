package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data;

import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoAvulsaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoPlacaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.ResponseAfericaoRodoparHorizonte;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RodoparHorizonteRequesterImpl implements RodoparHorizonteRequester {

    @NotNull
    @Override
    public String getTokenUsuarioIntegracao(@NotNull final String cpf,
                                            @NotNull final String dataNascimento) throws Throwable {
//        final RodoparHorizonteRest service = RodoparHorizonteRestClient.getService(RodoparHorizonteRest.class);
//        final Call<String> call = service.getTokenUsuarioIntegracao(cpf, dataNascimento);
//        return handleResponse(call.execute());
        return "dhauhds822dfas";
    }

    @NotNull
    @Override
    public ResponseAfericaoRodoparHorizonte insertAfericaoPlaca(
            @NotNull final String tokenIntegracao,
            @NotNull final AfericaoPlacaRodoparHorizonte afericao) throws Throwable {
        final RodoparHorizonteRest service = RodoparHorizonteRestClient.getService(RodoparHorizonteRest.class);
        final Call<ResponseAfericaoRodoparHorizonte> call =
                service.insertAfericaoPlaca(tokenIntegracao, afericao);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public ResponseAfericaoRodoparHorizonte insertAfericaoAvulsa(
            @NotNull final String tokenIntegracao,
            @NotNull final AfericaoAvulsaRodoparHorizonte afericao) throws Throwable {
        final RodoparHorizonteRest service = RodoparHorizonteRestClient.getService(RodoparHorizonteRest.class);
        final Call<ResponseAfericaoRodoparHorizonte> call =
                service.insertAfericaoAvulsa(tokenIntegracao, afericao);
        return handleResponse(call.execute());
    }

    @NotNull
    private <T> T handleResponse(@Nullable final Response<T> response) {
        //noinspection Duplicates
        if (response != null) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                if (response.errorBody() == null) {
                    throw new IllegalStateException("[INTEGRACAO - HORIZONTE] O corpo da requisição está vazio");
                }
                throw new IllegalStateException(
                        "[INTEGRACAO - HORIZONTE] A requisição retornou código de erro: " + response.code());
            }
        } else {
            throw new IllegalStateException("[INTEGRACAO - HORIZONTE] Nunhuma resposta obtida da integração");
        }
    }
}
