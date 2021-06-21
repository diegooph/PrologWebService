package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data;

import br.com.zalf.prolog.webservice.integracao.network.RestClient;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.AfericaoPlacaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.PneuEstoqueProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.VeiculoAfericaoProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.VeiculoListagemProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.error.ProtheusNepomucenoException;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido.*;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Response;

import java.util.List;

import static br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.ProtheusNepomucenoConstants.*;

/**
 * Created on 3/10/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class ProtheusNepomucenoRequesterImpl implements ProtheusNepomucenoRequester {
    @NotNull
    @Override
    public List<VeiculoListagemProtheusNepomuceno> getListagemVeiculosUnidadesSelecionadas(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final String codFiliais) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<List<VeiculoListagemProtheusNepomuceno>> call =
                service.getListagemVeiculosUnidadesSelecionadas(apiAutenticacaoHolder.getPrologTokenIntegracao(),
                                                                apiAutenticacaoHolder.getUrl(),
                                                                codFiliais);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public VeiculoAfericaoProtheusNepomuceno getPlacaPneusAfericaoPlaca(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final String codFilial,
            @NotNull final String placaVeiculo) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<VeiculoAfericaoProtheusNepomuceno> call =
                service.getPlacaPneusAfericaoPlaca(apiAutenticacaoHolder.getPrologTokenIntegracao(),
                                                   apiAutenticacaoHolder.getUrl(),
                                                   codFilial,
                                                   placaVeiculo);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public ResponseAfericaoProtheusNepomuceno insertAfericaoPlaca(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final AfericaoPlacaProtheusNepomuceno afericaoPlaca) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<ResponseAfericaoProtheusNepomuceno> call =
                service.insertAfericaoPlaca(apiAutenticacaoHolder.getPrologTokenIntegracao(),
                                            apiAutenticacaoHolder.getUrl(),
                                            afericaoPlaca);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public List<PneuListagemInspecaoRemovido> getListagemPneusInspecaoRemovido(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final String codFiliais) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<List<PneuListagemInspecaoRemovido>> call =
                service.getListagemPneusInspecaoRemovido(apiAutenticacaoHolder.getPrologTokenIntegracao(),
                                                         apiAutenticacaoHolder.getUrl(),
                                                         codFiliais);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public PneuEstoqueProtheusNepomuceno getPneuEmEstoqueAfericaoAvulsa(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final String codFilial,
            @NotNull final String codPneu) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<PneuEstoqueProtheusNepomuceno> call =
                service.getPneuEmEstoqueAfericaoAvulsa(apiAutenticacaoHolder.getPrologTokenIntegracao(),
                                                       apiAutenticacaoHolder.getUrl(),
                                                       codFilial,
                                                       codPneu);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public ResponseAfericaoProtheusNepomuceno insertInspecaoRemovido(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final InspecaoRemovidoRealizada inspecaoRemovido) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<ResponseAfericaoProtheusNepomuceno> call =
                service.insertInspecaoRemovido(apiAutenticacaoHolder.getPrologTokenIntegracao(),
                                               apiAutenticacaoHolder.getUrl(),
                                               inspecaoRemovido);
        return handleResponse(call.execute());
    }

    @Override
    @NotNull
    public Observable<List<LipPneuProtheusNepomuceno>> getLips(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final String codFilial) {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        return service.getLips(apiAutenticacaoHolder.getPrologTokenIntegracao(),
                               apiAutenticacaoHolder.getUrl(),
                               DEFAULT_QUERY_PARAM_LIPS,
                               codFilial);
    }

    @Override
    @NotNull
    public Observable<List<FilialProtheusNepomuceno>> getFiliais(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final String codFilial) {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        return service.getFiliais(apiAutenticacaoHolder.getPrologTokenIntegracao(),
                                  apiAutenticacaoHolder.getUrl(),
                                  DEFAULT_QUERY_PARAM_FILIAIS,
                                  codFilial);
    }

    @Override
    @NotNull
    public Observable<List<CausaSucataPneuProtheusNepomuceno>> getCausasSucata(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final String codFilial) {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        return service.getCausasSucata(apiAutenticacaoHolder.getPrologTokenIntegracao(),
                                       apiAutenticacaoHolder.getUrl(),
                                       DEFAULT_QUERY_PARAM_SUCATA,
                                       codFilial);
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
                final ErrorResponseProtheusNepomuceno protheusNepomucenoError =
                        toProtheusNepomucenoError(response.errorBody());
                throw new ProtheusNepomucenoException(
                        protheusNepomucenoError.getErrorCode(),
                        ProtheusNepomucenoException.getPrettyMessage(protheusNepomucenoError.getErrorMessage()));
            }
        } else {
            throw new ProtheusNepomucenoException(
                    "[INTEGRAÇÃO] Nenhuma resposta obtida do sistema Protheus-Nepomuceno");
        }
    }

    @NotNull
    private ErrorResponseProtheusNepomuceno toProtheusNepomucenoError(@NotNull final ResponseBody errorBody) {
        try {
            final String jsonErrorBody = errorBody.string();
            try {
                return ErrorResponseProtheusNepomuceno.generateFromString(jsonErrorBody);
            } catch (final Exception e) {
                // Lançamos essa Exception para conseguirmos encapsular o JSON de erro que não foi convertido.
                // Só assim conseguiremos tratar de forma mais eficaz.
                throw new Exception("Erro ao realizar parse da mensagem de erro: " + jsonErrorBody, e);
            }
        } catch (final Throwable t) {
            throw new ProtheusNepomucenoException(
                    "[INTEGRAÇÃO] Mensagem do sistema Protheus-Nepomuceno fora do padrão esperado",
                    "Não foi possível obter o JSON de resposta da requisição",
                    t);
        }
    }
}
