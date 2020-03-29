package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogError;
import br.com.zalf.prolog.webservice.integracao.network.RestClient;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.*;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.error.ProtheusNepomucenoException;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Response;

import java.util.List;

/**
 * Created on 3/10/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class ProtheusNepomucenoRequesterImpl implements ProtheusNepomucenoRequester {
    @NotNull
    @Override
    public List<VeiculoListagemProtheusNepomuceno> getListagemVeiculosUnidadesSelecionadas(
            @NotNull final String url,
            @NotNull final String codFiliais) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<List<VeiculoListagemProtheusNepomuceno>> call =
                service.getListagemVeiculosUnidadesSelecionadas(url, codFiliais);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public VeiculoAfericaoProtheusNepomuceno getPlacaPneusAfericaoPlaca(
            @NotNull final String url,
            @NotNull final String codFilial,
            @NotNull final String placaVeiculo) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<VeiculoAfericaoProtheusNepomuceno> call =
                service.getPlacaPneusAfericaoPlaca(url, codFilial, placaVeiculo);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public ResponseAfericaoProtheusNepomuceno insertAfericaoPlaca(
            @NotNull final String url,
            @NotNull final AfericaoPlacaProtheusNepomuceno afericaoPlaca) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<ResponseAfericaoProtheusNepomuceno> call = service.insertAfericaoPlaca(url, afericaoPlaca);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public List<PneuEstoqueProtheusNepomuceno> getListagemPneusEmEstoque(
            @NotNull final String url,
            @NotNull final String codFiliais) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<List<PneuEstoqueProtheusNepomuceno>> call = service.getListagemPneusEmEstoque(url, codFiliais);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public PneuEstoqueProtheusNepomuceno getPneuEmEstoqueAfericaoAvulsa(
            @NotNull final String url,
            @NotNull final String codFilial,
            @NotNull final String codPneu) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<PneuEstoqueProtheusNepomuceno> call =
                service.getPneuEmEstoqueAfericaoAvulsa(url, codFilial, codPneu);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public ResponseAfericaoProtheusNepomuceno insertAfericaoAvulsa(
            @NotNull final String url,
            @NotNull final AfericaoAvulsaProtheusNepomuceno afericaoAvulsa) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<ResponseAfericaoProtheusNepomuceno> call = service.insertAfericaoAvulsa(url, afericaoAvulsa);
        return handleResponse(call.execute());
    }

    @NotNull
    private <T> T handleResponse(@Nullable final Response<T> response) {
        if (response != null) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                // Aqui significa que a resposta recebida é um erro.
                if (response.errorBody() == null) {
                    throw new ProtheusNepomucenoException(
                            "[INTEGRAÇÃO] Nenhuma resposta obtida do sistema Protheus-Nepomuceno");
                }
                final ProLogError proLogError = toProLogError(response.errorBody());
                throw new ProtheusNepomucenoException(
                        proLogError.getHttpStatusCode(),
                        ProtheusNepomucenoException.getPrettyMessage(proLogError.getMessage()));
            }
        } else {
            throw new ProtheusNepomucenoException(
                    "[INTEGRAÇÃO] Nenhuma resposta obtida do sistema Protheus-Nepomuceno");
        }
    }

    @NotNull
    private ProLogError toProLogError(@NotNull final ResponseBody errorBody) {
        try {
            final String jsonErrorBody = errorBody.string();
            return ProLogError.generateFromString(jsonErrorBody);
        } catch (final Throwable t) {
            throw new ProtheusNepomucenoException(
                    "[INTEGRAÇÃO] Mensagem do sistema Protheus-Nepomuceno fora do padrão esperado",
                    "Não foi possível obter o JSON de resposta da requisição",
                    t);
        }
    }
}
