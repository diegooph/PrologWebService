package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data;

import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoAvulsaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoPlacaRodoparHorizonte;
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

    @FormUrlEncoded
    @POST("token")
    Call<RodoparToken> getTokenUsuarioIntegracao(@Field("username") String username,
                                           @Field("password") String password,
                                           @Field("grant_type") String grant_type);

    @POST("api/AfericaoRealizada")
    Call<ResponseAfericaoRodoparHorizonte> insertAfericaoPlaca(
            @Header("authorization") @NotNull final String tokenIntegracao,
            @Body @NotNull final AfericaoPlacaRodoparHorizonte afericao);

    @POST("api/AfericaoRealizada")
    Call<ResponseAfericaoRodoparHorizonte> insertAfericaoAvulsa(
            @Header("authorization") @NotNull final String tokenIntegracao,
            @Body @NotNull final AfericaoAvulsaRodoparHorizonte afericao);
}
