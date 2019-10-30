package br.com.zalf.prolog.webservice.integracao.api;

import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created on 10/29/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface SistemaApiProLogRest {
    @POST
    Call<SuccessResponseIntegracao> insertProcessoMovimentacao(@Url @NotNull final String url);
}
