package br.com.zalf.prolog.webservice.integracao.protheusrodalog;

import br.com.zalf.prolog.webservice.integracao.protheusrodalog.data.ProtheusRodalogRest;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.data.ProtheusRodalogRestClient;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.*;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.error.ProtheusRodalogException;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.error.RodoparHorizonteException;
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
    public ProtheusRodalogResponseAfericao insertAfericao(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUnidade,
            @NotNull final AfericaoProtheusRodalog afericao) throws Throwable {
        final ProtheusRodalogRest service = ProtheusRodalogRestClient.getService(ProtheusRodalogRest.class);
        final Call<ProtheusRodalogResponseAfericao> call = service.insertAfericao(tokenIntegracao, codUnidade, afericao);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public CronogramaAfericaoProtheusRodalog getCronogramaAfericao(@NotNull final String tokenIntegracao,
                                                                   @NotNull final Long codUnidade) throws Throwable {
        final ProtheusRodalogRest service = ProtheusRodalogRestClient.getService(ProtheusRodalogRest.class);
        final Call<CronogramaAfericaoProtheusRodalog> call = service.getCronogramaAfericao(tokenIntegracao, codUnidade);
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
                    throw new ProtheusRodalogException(
                            "[INTEGRACAO - RODALOG] Protheus não retornou todas as informações necessárias",
                            "A comunicação retornou erro porém sem nenhuma informação no corpo do erro");
                }
                throw new ProtheusRodalogException(
                        "[INTEGRACAO - RODALOG] Protheus não conseguiu processar as informações",
                        "A requisição do token retornou erro, provavelmente falta mapeamento no Protheus");
            }
        } else {
            throw new RodoparHorizonteException(
                    "[INTEGRACAO - RODALOG] Nunhuma resposta obtida da integração com o sistema Protheus",
                    "A comunicação com o Protheus retornou um response vazio");
        }
    }
}
