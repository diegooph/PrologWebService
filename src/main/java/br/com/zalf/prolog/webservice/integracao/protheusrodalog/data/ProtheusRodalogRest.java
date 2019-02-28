package br.com.zalf.prolog.webservice.integracao.protheusrodalog.data;

import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.AfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.CronogramaAfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.NovaAfericaoPlacaProtheusRodalog;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created on 28/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ProtheusRodalogRest {

    // TODO - Setar o PATH da requisição e os parâmetros necessários

    @POST("")
    Call<Long> insertAfericao(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Query("codUnidade") @Required final Long codUnidade,
            @Body @Required final AfericaoProtheusRodalog afericao);

    @GET("")
    Call<CronogramaAfericaoProtheusRodalog> getCronogramaAfericao(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Query("codUnidade") @Required final Long codUnidade);

    @GET("")
    Call<NovaAfericaoPlacaProtheusRodalog> getNovaAfericaoPlaca(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Query("codUnidade") @Required final Long codUnidade,
            @Query("placa") @Required final String placa,
            @Query("tipoAfericao") @Required final String tipoAfericao);
}
