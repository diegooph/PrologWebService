package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.*;
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
    Call<List<PneuEstoqueProtheusNepomuceno>> getListagemPneusEmEstoque(
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
    Call<ResponseAfericaoProtheusNepomuceno> insertAfericaoAvulsa(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Body @NotNull final AfericaoAvulsaProtheusNepomuceno afericaoAvulsa);
}
