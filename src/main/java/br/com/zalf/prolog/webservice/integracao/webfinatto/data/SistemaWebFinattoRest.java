package br.com.zalf.prolog.webservice.integracao.webfinatto.data;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.EmpresaWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.PneuWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.VeiculoWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.afericao.AfericaoPlacaWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.afericao.AfericaoPneuWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.movimentacao.ProcessoMovimentacaoWebFinatto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SistemaWebFinattoRest {
    @GET()
    Call<List<EmpresaWebFinatto>> getFiltrosClientes(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("cpfColaborador") @NotNull final String cpfColaborador);

    @GET()
    Call<List<VeiculoWebFinatto>> getVeiculosByFiliais(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("codFiliais") @NotNull final String codFiliais,
            @Query("placaVeiculo") @Nullable final String placaVeiculo);

    @GET()
    Call<VeiculoWebFinatto> getVeiculoByPlaca(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("codFilial") @NotNull final String codFilial,
            @Query("placaSelecionada") @NotNull final String placaSelecionada);

    @GET()
    Call<List<PneuWebFinatto>> getPneusByFiliais(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("codFiliais") @NotNull final String codFiliais,
            @Query("statusPneus") @Nullable final String statusPneus,
            @Query("codPneu") @Nullable final String codPneu);

    @GET()
    Call<PneuWebFinatto> getPneuByCodigo(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("codFilial") @NotNull final String codFilial,
            @Query("codPneuSelecionado") @NotNull final String codPneuSelecionado);

    @POST()
    Call<Void> insertAfericaoPlaca(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Body @NotNull final AfericaoPlacaWebFinatto afericaoPlaca);

    @POST()
    Call<Void> insertAfericaoAvulsa(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Body @NotNull final AfericaoPneuWebFinatto afericaoPneu);

    @POST()
    Call<Void> insertProcessoMovimentacao(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Body @NotNull final ProcessoMovimentacaoWebFinatto processoMovimentacao);
}
