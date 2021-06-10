package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.AfericaoPlacaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.PneuEstoqueProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.VeiculoAfericaoProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.VeiculoListagemProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido.*;
import io.reactivex.rxjava3.core.Observable;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * Created on 3/10/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface ProtheusNepomucenoRest {

    @GET()
    Call<List<VeiculoListagemProtheusNepomuceno>> getListagemVeiculosUnidadesSelecionadas(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("codFiliais") @NotNull final String codFiliais);

    @GET()
    Call<VeiculoAfericaoProtheusNepomuceno> getPlacaPneusAfericaoPlaca(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("codFilial") @NotNull final String codFilial,
            @Query("placaVeiculo") @NotNull final String placaVeiculo);

    @POST()
    Call<ResponseAfericaoProtheusNepomuceno> insertAfericaoPlaca(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Body @NotNull final AfericaoPlacaProtheusNepomuceno afericaoPlaca);

    @GET()
    Call<List<PneuListagemInspecaoRemovido>> getListagemPneusInspecaoRemovido(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("codFiliais") @NotNull final String codFiliais);

    @GET()
    Call<PneuEstoqueProtheusNepomuceno> getPneuEmEstoqueAfericaoAvulsa(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("codFilial") @NotNull final String codFilial,
            @Query("codPneu") @NotNull final String codPneu);

    @POST()
    Call<ResponseAfericaoProtheusNepomuceno> insertInspecaoRemovido(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Body @NotNull final InspecaoRemovidoRealizada inspecaoRemovido);

    @GET()
    Observable<List<LipPneuProtheusNepomuceno>> getLips(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("listatabela") @NotNull final String lips,
            @Query("codFilial") @NotNull final String codFilial);

    @GET()
    Observable<List<FilialProtheusNepomuceno>> getFiliais(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("listatabela") @NotNull final String filiais,
            @Query("codFilial") @NotNull final String codFilial);

    @GET()
    Observable<List<CausaSucataPneuProtheusNepomuceno>> getCausasSucata(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("listatabela") @NotNull final String sucata,
            @Query("codFilial") @NotNull final String codFilial);
}
