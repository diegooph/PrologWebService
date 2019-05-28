package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data;

import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.*;
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
    public ResponseAfericaoRodoparHorizonte insertAfericao(
            @NotNull final String cpf,
            @NotNull final String dataNascimento,
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUnidade,
            @NotNull final AfericaoRodoparHorizonte afericao) throws Throwable {
        final RodoparHorizonteRest service = RodoparHorizonteRestClient.getService(RodoparHorizonteRest.class);
        final Call<ResponseAfericaoRodoparHorizonte> call =
                service.insertAfericao(cpf, dataNascimento, codUnidade, afericao);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public CronogramaAfericaoRodoparHorizonte getCronogramaAfericao(@NotNull final String cpf,
                                                                    @NotNull final String dataNascimento,
                                                                    @NotNull final String tokenIntegracao,
                                                                    @NotNull final Long codUnidade) throws Throwable {
        final RodoparHorizonteRest service = RodoparHorizonteRestClient.getService(RodoparHorizonteRest.class);
        final Call<CronogramaAfericaoRodoparHorizonte> call =
                service.getCronogramaAfericao(cpf, dataNascimento, codUnidade);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public NovaAfericaoPlacaRodoparHorizonte getNovaAfericaoPlaca(
            @NotNull final String cpf,
            @NotNull final String dataNascimento,
            @NotNull final Long codUnidade,
            @NotNull final String placa,
            @NotNull final TipoMedicaoAfericaoRodoparHorizonte tipoAfericao) throws Throwable {
        final RodoparHorizonteRest service = RodoparHorizonteRestClient.getService(RodoparHorizonteRest.class);
        final Call<NovaAfericaoPlacaRodoparHorizonte> call =
                service.getNovaAfericaoPlaca(cpf, dataNascimento, codUnidade, placa, tipoAfericao.asString());
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
