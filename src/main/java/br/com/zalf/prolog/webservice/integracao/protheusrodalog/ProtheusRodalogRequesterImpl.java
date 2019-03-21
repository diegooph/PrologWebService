package br.com.zalf.prolog.webservice.integracao.protheusrodalog;

import br.com.zalf.prolog.webservice.integracao.protheusrodalog.data.ProtheusRodalogRest;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.data.ProtheusRodalogRestClient;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.AfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.CronogramaAfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.NovaAfericaoPlacaProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.TipoMedicaoAfericaoProtheusRodalog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ProtheusRodalogRequesterImpl implements ProtheusRodalogRequester {
    @NotNull
    @Override
    public Long insertAfericao(@NotNull final String tokenIntegracao,
                               @NotNull final Long codUnidade,
                               @NotNull final AfericaoProtheusRodalog afericao) throws Throwable {
        final ProtheusRodalogRest service = ProtheusRodalogRestClient.getService(ProtheusRodalogRest.class);
        final Call<Long> call = service.insertAfericao(tokenIntegracao, codUnidade, afericao);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public CronogramaAfericaoProtheusRodalog getCronogramaAfericao(@NotNull final String tokenIntegracao,
                                                                   @NotNull final Long codUnidade) throws Throwable {
        final ProtheusRodalogRest service = ProtheusRodalogRestClient.getService(ProtheusRodalogRest.class);
        final Call<CronogramaAfericaoProtheusRodalog> call =
                service.getCronogramaAfericao(tokenIntegracao, codUnidade);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public NovaAfericaoPlacaProtheusRodalog getNovaAfericaoPlaca(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUnidade,
            @NotNull final String placa,
            @NotNull final TipoMedicaoAfericaoProtheusRodalog tipoAfericao) throws Throwable {
        final ProtheusRodalogRest service = ProtheusRodalogRestClient.getService(ProtheusRodalogRest.class);
        final Call<NovaAfericaoPlacaProtheusRodalog> call =
                service.getNovaAfericaoPlaca(tokenIntegracao, codUnidade, placa, tipoAfericao.asString());
        return handleResponse(call.execute());
    }

    @NotNull
    private <T> T handleResponse(@Nullable final Response<T> response) {
        if (response != null) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                if (response.errorBody() == null) {
                    throw new IllegalStateException("[INTEGRACAO - RODALOG] O corpo da requisição está vazio");
                }
                throw new IllegalStateException(
                        "[INTEGRACAO - RODALOG] A requisição retornou código de erro: " + response.code());
            }
        } else {
            throw new IllegalStateException("[INTEGRACAO - RODALOG] Nunhuma resposta obtida da integração");
        }
    }
}
