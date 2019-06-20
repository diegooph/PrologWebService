package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data;

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
    @POST(RodoparHorizonteConstants.URL_TOKEN_INTEGRACAO)
    Call<RodoparHorizonteTokenIntegracao> getTokenUsuarioIntegracao(
            @Field(RodoparHorizonteConstants.FIELD_USERNAME) @NotNull final String username,
            @Field(RodoparHorizonteConstants.FIELD_PASSWORD) @NotNull final String password,
            @Field(RodoparHorizonteConstants.FIELD_GRANT_TYPE) @NotNull final String grant_type);

    @POST(RodoparHorizonteConstants.URL_AFERICAO_PLACA_INTEGRACAO)
    Call<ResponseAfericaoRodoparHorizonte> insertAfericaoPlaca(
            @Header(RodoparHorizonteConstants.HEADER_AUTHORIZATION) @NotNull final String tokenIntegracao,
            @Body @NotNull final AfericaoPlacaRodoparHorizonte afericao);

    @POST(RodoparHorizonteConstants.URL_AFERICAO_AVULSA_INTEGRACAO)
    Call<ResponseAfericaoRodoparHorizonte> insertAfericaoAvulsa(
            @Header(RodoparHorizonteConstants.HEADER_AUTHORIZATION) @NotNull final String tokenIntegracao,
            @Body @NotNull final AfericaoAvulsaRodoparHorizonte afericao);
}
