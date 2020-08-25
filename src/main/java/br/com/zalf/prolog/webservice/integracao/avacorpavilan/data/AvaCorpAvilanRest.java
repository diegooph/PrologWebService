package br.com.zalf.prolog.webservice.integracao.avacorpavilan.data;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.os._model.OsAvilan;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created on 2020-08-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface AvaCorpAvilanRest {

    @POST
    Call<Response> insertChecklistOs(
            @Url @NotNull final String url,
            @Body @NotNull final OsAvilan osAvilan);

}
