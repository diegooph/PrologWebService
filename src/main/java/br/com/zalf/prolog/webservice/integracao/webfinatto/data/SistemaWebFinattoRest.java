package br.com.zalf.prolog.webservice.integracao.webfinatto.data;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SistemaWebFinattoRest {
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
    Call<List<PneuWebFinatto>> getPneusByCodigo(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("codFilial") @NotNull final String codFilial,
            @Query("codPneuSelecionado") @NotNull final String codPneuSelecionado);

    @POST()
    Call<ResponseAfericaoWebFinatto> insertAfericaoPlaca(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Body @NotNull final AfericaoPlacaWebFinatto afericaoPlaca);

    @POST()
    Call<ResponseAfericaoWebFinatto> insertAfericaoAvulsa(
            @Header(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Body @NotNull final AfericaoPneuWebFinatto afericaoPneu);
}
