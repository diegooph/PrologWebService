package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data;

import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoAvulsaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoPlacaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.ResponseAfericaoRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token.RodoparCredentials;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token.RodoparHorizonteTokenIntegracao;
import br.com.zalf.prolog.webservice.integracao.sistema.Requester;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface RodoparHorizonteRequester extends Requester {

    @NotNull
    RodoparHorizonteTokenIntegracao getTokenUsuarioIntegracao(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder,
            @NotNull final RodoparCredentials credentials) throws Throwable;

    @NotNull
    ResponseAfericaoRodoparHorizonte insertAfericaoPlaca(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder,
            @NotNull final String tokenIntegracao,
            @NotNull final AfericaoPlacaRodoparHorizonte afericao) throws Throwable;

    @NotNull
    ResponseAfericaoRodoparHorizonte insertAfericaoAvulsa(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder,
            @NotNull final String tokenIntegracao,
            @NotNull final AfericaoAvulsaRodoparHorizonte afericao) throws Throwable;
}
