package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data;

import br.com.zalf.prolog.webservice.integracao.network.RestClient;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model.AfericaoAvulsaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model.AfericaoPlacaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model.PneuEstoqueProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.error.ProtheusRodalogException;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.error.RodoparHorizonteException;
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
    public ResponseAfericaoProtheusNepomuceno insertAfericaoPlaca(
            @NotNull final AfericaoPlacaProtheusNepomuceno afericaoPlaca) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<ResponseAfericaoProtheusNepomuceno> call =
                service.insertAfericaoPlaca(afericaoPlaca);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public ResponseAfericaoProtheusNepomuceno insertAfericaoAvulsa(
            @NotNull final AfericaoAvulsaProtheusNepomuceno afericaoAvulsa) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<ResponseAfericaoProtheusNepomuceno> call =
                service.insertAfericaoAvulsa(afericaoAvulsa);
        return handleResponse(call.execute());
    }

    @NotNull
    @Override
    public List<PneuEstoqueProtheusNepomuceno> getListagemPneusEmEstoque(@NotNull final String codFiliais) throws Throwable {
        final ProtheusNepomucenoRest service = RestClient.getService(ProtheusNepomucenoRest.class);
        final Call<List<PneuEstoqueProtheusNepomuceno>> call =
                service.getListagemPneusEmEstoque(codFiliais);
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
                            "[INTEGRACAO - NEPOMUCENO] Protheus não retornou todas as informações necessárias",
                            "A comunicação retornou erro porém sem nenhuma informação no corpo do erro");
                }
                throw new ProtheusRodalogException(
                        "[INTEGRACAO - NEPOMUCENO] Protheus não conseguiu processar as informações",
                        "A requisição do token retornou erro, provavelmente falta mapeamento no Protheus");
            }
        } else {
            throw new RodoparHorizonteException(
                    "[INTEGRACAO - NEPOMUCENO] Nunhuma resposta obtida da integração com o sistema Protheus",
                    "A comunicação com o Protheus retornou um response vazio");
        }
    }
}
