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

    @GET("")
    Call<String> getTokenUsuarioIntegracao(@Header("user") @NotNull final String cpf,
                                           @Header("pass") @NotNull final String dataNascimento);

    // TODO - Estruturar todos os endpoints - Preciso das informações do WS parceiro.
    // TODO - Descobrir nome das propriedades que irão no Header da requisição.
    @POST("")
    Call<ResponseAfericaoRodoparHorizonte> insertAfericaoPlaca(
            @Header("token") @NotNull final String tokenIntegracao,
            @Body @NotNull final AfericaoPlacaRodoparHorizonte afericao);

    @POST("")
    Call<ResponseAfericaoRodoparHorizonte> insertAfericaoAvulsa(
            @Header("token") @NotNull final String tokenIntegracao,
            @Body @NotNull final AfericaoAvulsaRodoparHorizonte afericao);
}
