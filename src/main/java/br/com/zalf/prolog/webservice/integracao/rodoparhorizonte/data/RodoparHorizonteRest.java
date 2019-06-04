package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data;

import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.ResponseAfericaoRodoparHorizonte;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface RodoparHorizonteRest {

    // TODO - Estruturar todos os endpoints - Preciso das informações do WS parceiro.
    // TODO - Descobrir nome das propriedades que irão no Header da requisição.
    @POST("")
    Call<ResponseAfericaoRodoparHorizonte> insertAfericao(
            @Header("user") @NotNull final String cpf,
            @Header("pass") @NotNull final String dataNascimento,
            @Query("codUnidade") @NotNull final Long codUnidade,
            @Body @NotNull final AfericaoRodoparHorizonte afericao);
}
