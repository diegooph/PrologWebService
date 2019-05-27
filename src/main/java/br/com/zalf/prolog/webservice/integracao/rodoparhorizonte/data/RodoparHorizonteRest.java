package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data;

import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.AfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.CronogramaAfericaoRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.NovaAfericaoPlacaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.ResponseAfericaoRodoparHorizonte;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface RodoparHorizonteRest {
    
    @POST("NEWAFERI")
    Call<ResponseAfericaoRodoparHorizonte> insertAfericao(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Query("codUnidade") @NotNull final Long codUnidade,
            @Body @NotNull final AfericaoProtheusRodalog afericao);

    @GET("CRONOGRAMA")
    Call<CronogramaAfericaoRodoparHorizonte> getCronogramaAfericao(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Query("codUnidade") @NotNull final Long codUnidade);

    @GET("NEWAFERI")
    Call<NovaAfericaoPlacaRodoparHorizonte> getNovaAfericaoPlaca(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Query("codUnidade") @NotNull final Long codUnidade,
            @Query("placa") @NotNull final String placa,
            @Query("tipoAfericao") @NotNull final String tipoAfericao);
}
