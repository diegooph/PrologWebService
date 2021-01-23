package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data;

import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.RodoparHorizonteConstants;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoAvulsaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoPlacaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.ResponseAfericaoRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token.RodoparHorizonteTokenIntegracao;
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
    @POST()
    Call<RodoparHorizonteTokenIntegracao> getTokenUsuarioIntegracao(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Field(RodoparHorizonteConstants.FIELD_USERNAME) @NotNull final String username,
            @Field(RodoparHorizonteConstants.FIELD_PASSWORD) @NotNull final String password,
            @Field(RodoparHorizonteConstants.FIELD_GRANT_TYPE) @NotNull final String grantType);

    @POST()
    Call<ResponseAfericaoRodoparHorizonte> insertAfericaoPlaca(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracaoProlog,
            @Header(RodoparHorizonteConstants.HEADER_AUTHORIZATION) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Body @NotNull final AfericaoPlacaRodoparHorizonte afericao);

    @POST()
    Call<ResponseAfericaoRodoparHorizonte> insertAfericaoAvulsa(
            @Header(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @NotNull final String tokenIntegracaoProlog,
            @Header(RodoparHorizonteConstants.HEADER_AUTHORIZATION) @NotNull final String tokenIntegracao,
            @Url @NotNull final String url,
            @Body @NotNull final AfericaoAvulsaRodoparHorizonte afericao);
}
