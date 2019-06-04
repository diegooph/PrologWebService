package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data;

import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoAvulsaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoPlacaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.ResponseAfericaoRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.sistema.Requester;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface RodoparHorizonteRequester extends Requester {

    @NotNull
    ResponseAfericaoRodoparHorizonte insertAfericaoPlaca(
            @NotNull final String cpf,
            @NotNull final String dataNascimento,
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUnidade,
            @NotNull final AfericaoPlacaRodoparHorizonte afericao) throws Throwable;

    @NotNull
    ResponseAfericaoRodoparHorizonte insertAfericaoAvulsa(
            @NotNull final String cpf,
            @NotNull final String dataNascimento,
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUnidade,
            @NotNull final AfericaoAvulsaRodoparHorizonte afericao) throws Throwable;
}
