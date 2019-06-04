package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data;

import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.*;
import br.com.zalf.prolog.webservice.integracao.sistema.Requester;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface RodoparHorizonteRequester extends Requester {

    @NotNull
    ResponseAfericaoRodoparHorizonte insertAfericao(@NotNull final String cpf,
                                                    @NotNull final String dataNascimento,
                                                    @NotNull final String tokenIntegracao,
                                                    @NotNull final Long codUnidade,
                                                    @NotNull final AfericaoRodoparHorizonte afericao) throws Throwable;
}
