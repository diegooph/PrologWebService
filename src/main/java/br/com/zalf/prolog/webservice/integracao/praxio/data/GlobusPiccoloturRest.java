package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.GlobusPiccoloturLocaisMovimento;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.ProcessoMovimentacaoGlobus;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * Created on 11/12/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface GlobusPiccoloturRest {
    @GET
    Call<GlobusPiccoloturAutenticacaoResponse> getTokenAutenticacaoIntegracao(
            @Url @NotNull final String url,
            @Query("token") @NotNull final String token,
            @Query("shortCode") @NotNull final Long shortCode);

    @POST
    Call<GlobusPiccoloturMovimentacaoResponse> insertProcessoMovimentacao(
            @Url @NotNull final String url,
            @Header("authorization") @NotNull final String tokenIntegracao,
            @Body @NotNull final ProcessoMovimentacaoGlobus processoMovimentacaoGlobus);

    @POST
    Call<List<GlobusPiccoloturLocaisMovimento>> getLocaisMovimentoGlobus(
            @Url @NotNull final String url,
            @Header("authorization") @NotNull final String tokenIntegracao,
            @Query("cpf") @NotNull final String cpfUsuario);
}
