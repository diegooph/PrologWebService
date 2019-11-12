package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.ProcessoMovimentacaoGlobus;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created on 11/12/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface GlobusPiccoloturRest {
    @POST
    Call<SuccessResponseIntegracao> insertProcessoMovimentacao(
            @Url @NotNull final String url,
            @Body @NotNull final ProcessoMovimentacaoGlobus processoMovimentacaoGlobus);
}
