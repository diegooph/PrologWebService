package br.com.zalf.prolog.webservice.integracao.avacorpavilan.data;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.OrdemServicoAvaCorpAvilan;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created on 2020-08-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface AvaCorpAvilanRest {
    @POST
    Call<Void> insertChecklistOs(
            @Header("Authorization") @NotNull final String token,
            @Url @NotNull final String url,
            @Body @NotNull final OrdemServicoAvaCorpAvilan ordemServicoAvaCorpAvilan);
}
