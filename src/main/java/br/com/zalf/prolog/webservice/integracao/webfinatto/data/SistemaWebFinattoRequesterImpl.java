package br.com.zalf.prolog.webservice.integracao.webfinatto.data;

import br.com.zalf.prolog.webservice.integracao.network.RestClient;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.*;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.error.SistemaWebFinattoException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Response;

import java.util.List;

public class SistemaWebFinattoRequesterImpl implements SistemaWebFinattoRequester {
    @Override
    @NotNull
    public List<VeiculoWebFinatto> getVeiculosByFiliais(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                                        @NotNull final String codFiliais,
                                                        @Nullable final String placaVeiculo) throws Throwable {
        final SistemaWebFinattoRest service = RestClient.getService(SistemaWebFinattoRest.class);
        final Call<List<VeiculoWebFinatto>> call =
                service.getVeiculosByFiliais(autenticacaoHolder.getPrologTokenIntegracao(),
                                             autenticacaoHolder.getUrl(),
                                             codFiliais,
                                             placaVeiculo);
        return handleResponse(call.execute());
    }

    @Override
    @NotNull
    public VeiculoWebFinatto getVeiculoByPlaca(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                               @NotNull final String codFilial,
                                               @NotNull final String placaSelecionada) throws Throwable {
        final SistemaWebFinattoRest service = RestClient.getService(SistemaWebFinattoRest.class);
        final Call<VeiculoWebFinatto> call =
                service.getVeiculoByPlaca(autenticacaoHolder.getPrologTokenIntegracao(),
                                          autenticacaoHolder.getUrl(),
                                          codFilial,
                                          placaSelecionada);
        return handleResponse(call.execute());
    }

    @Override
    @NotNull
    public List<PneuWebFinatto> getPneusByFiliais(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                                  @NotNull final String codFiliais,
                                                  @Nullable final String statusPneus,
                                                  @Nullable final String codPneu) throws Throwable {
        final SistemaWebFinattoRest service = RestClient.getService(SistemaWebFinattoRest.class);
        final Call<List<PneuWebFinatto>> call =
                service.getPneusByFiliais(autenticacaoHolder.getPrologTokenIntegracao(),
                                          autenticacaoHolder.getUrl(),
                                          codFiliais,
                                          statusPneus,
                                          codPneu);
        return handleResponse(call.execute());
    }

    @Override
    @NotNull
    public PneuWebFinatto getPneuByCodigo(@NotNull final ApiAutenticacaoHolder autenticacaoHolder,
                                          @NotNull final String codFilial,
                                          @NotNull final String codPneuSelecionado) throws Throwable {
        final SistemaWebFinattoRest service = RestClient.getService(SistemaWebFinattoRest.class);
        final Call<PneuWebFinatto> call =
                service.getPneuByCodigo(autenticacaoHolder.getPrologTokenIntegracao(),
                                        autenticacaoHolder.getUrl(),
                                        codFilial,
                                        codPneuSelecionado);
        return handleResponse(call.execute());
    }

    @Override
    @NotNull
    public ResponseAfericaoWebFinatto insertAfericaoPlaca(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder,
            @NotNull final AfericaoPlacaWebFinatto afericaoPlaca) throws Throwable {
        final SistemaWebFinattoRest service = RestClient.getService(SistemaWebFinattoRest.class);
        final Call<ResponseAfericaoWebFinatto> call =
                service.insertAfericaoPlaca(autenticacaoHolder.getPrologTokenIntegracao(),
                                            autenticacaoHolder.getUrl(),
                                            afericaoPlaca);
        return handleResponse(call.execute());
    }

    @Override
    @NotNull
    public ResponseAfericaoWebFinatto insertAfericaoAvulsa(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder,
            @NotNull final AfericaoPneuWebFinatto afericaoPneu) throws Throwable {
        final SistemaWebFinattoRest service = RestClient.getService(SistemaWebFinattoRest.class);
        final Call<ResponseAfericaoWebFinatto> call =
                service.insertAfericaoAvulsa(autenticacaoHolder.getPrologTokenIntegracao(),
                                             autenticacaoHolder.getUrl(),
                                             afericaoPneu);
        return handleResponse(call.execute());
    }

    @NotNull
    private <T> T handleResponse(@Nullable final Response<T> response) {
        if (response != null) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                if (response.errorBody() == null) {
                    throw new SistemaWebFinattoException("[INTEGRAÇÃO] Nenhuma resposta obtida do sistema WebFinatto");
                }
                throw SistemaWebFinattoException.from(response.errorBody());
            }
        } else {
            throw new SistemaWebFinattoException("[INTEGRAÇÃO] Nenhuma resposta obtida do sistema WebFinatto");
        }
    }
}
