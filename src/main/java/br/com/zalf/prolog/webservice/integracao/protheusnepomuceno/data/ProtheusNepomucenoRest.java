package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data;

import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model.AfericaoAvulsaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model.AfericaoPlacaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model.VeiculoAfericaoProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model.VeiculoListagemProtheusNepomuceno;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created on 3/10/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface ProtheusNepomucenoRest {

    @GET()
    Call<VeiculoListagemProtheusNepomuceno> getListagemVeiculosUnidadesSelecionadas(
            @Query("codFiliais") @NotNull final String codFiliais);

    @GET("NEWAFERI")
    Call<VeiculoAfericaoProtheusNepomuceno> getPlacaPneusAfericaoPlaca(
            @Query("codFilial") @NotNull final String codFilial,
            @Query("placaVeiculo") @NotNull final String placaVeiculo);

    @POST()
    Call<ResponseAfericaoProtheusNepomuceno> insertAfericaoPlaca(
            @Body @NotNull final AfericaoPlacaProtheusNepomuceno afericaoPlaca);

    @GET()
    Call<Object> getListagemPneusEmEstoque(
            @Query("codFiliais") @NotNull final String codFiliais);

    @GET("NEWAFERI")
    Call<Object> getPneuEmEstoqueAfericaoAvulsa(
            @Query("codFilial") @NotNull final String codFilial,
            @Query("placaVeiculo") @NotNull final String placaVeiculo);

    @POST()
    Call<ResponseAfericaoProtheusNepomuceno> insertAfericaoAvulsa(
            @Body @NotNull final AfericaoAvulsaProtheusNepomuceno afericaoAvulsa);
}
