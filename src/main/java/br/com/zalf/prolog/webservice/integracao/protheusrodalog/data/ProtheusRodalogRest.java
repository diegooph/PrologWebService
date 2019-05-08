package br.com.zalf.prolog.webservice.integracao.protheusrodalog.data;

import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.AfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.CronogramaAfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.NovaAfericaoPlacaProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.ProtheusRodalogResponseAfericao;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created on 28/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ProtheusRodalogRest {

    // Utilizamos a anotação 'Connection:close' para que o servidor force o fim da conexão ao receber os dados.
    // Inserimos, de forma forçada, o content-type no header da requisição. Por algum motivo estranho, o json estava
    // sendo enviado com o content-type correto mas não era interpretado. Forçando, então, passou a funcionar.
    @Headers({"Connection:close", "Content-Type: application/json"})
    @POST("NEWAFERI")
    Call<ProtheusRodalogResponseAfericao> insertAfericao(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Query("codUnidade") @NotNull final Long codUnidade,
            @Body @NotNull final AfericaoProtheusRodalog afericao);

    @Headers("Connection:close")
    @GET("CRONOGRAMA")
    Call<CronogramaAfericaoProtheusRodalog> getCronogramaAfericao(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Query("codUnidade") @NotNull final Long codUnidade);

    @Headers("Connection:close")
    @GET("NEWAFERI")
    Call<NovaAfericaoPlacaProtheusRodalog> getNovaAfericaoPlaca(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Query("codUnidade") @NotNull final Long codUnidade,
            @Query("placa") @NotNull final String placa,
            @Query("tipoAfericao") @NotNull final String tipoAfericao);
}
