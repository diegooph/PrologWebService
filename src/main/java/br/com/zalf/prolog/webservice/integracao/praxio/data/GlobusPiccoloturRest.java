package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.GlobusPiccoloturLocalMovimentoResponse;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.ProcessoMovimentacaoGlobus;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created on 11/12/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface GlobusPiccoloturRest {
    @GET
    Call<GlobusPiccoloturAutenticacaoResponse> getTokenAutenticacaoIntegracao(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Query("token") @NotNull final String tokenClient,
            @Query("shortCode") @NotNull final Long shortCode);

    @POST
    Call<GlobusPiccoloturMovimentacaoResponse> insertProcessoMovimentacao(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Header("authorization") @NotNull final String tokenClient,
            @Body @NotNull final ProcessoMovimentacaoGlobus processoMovimentacaoGlobus);

    @GET
    Call<GlobusPiccoloturLocalMovimentoResponse> getLocaisMovimentoGlobus(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Header("authorization") @NotNull final String tokenClient,
            @Query("cpf") @NotNull final String cpfUsuario);
}
