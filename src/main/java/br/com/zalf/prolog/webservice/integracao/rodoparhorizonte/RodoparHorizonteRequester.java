package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte;

import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.CronogramaAfericaoRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.NovaAfericaoPlacaRodoparHorizonte;
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
    ResponseAfericaoRodoparHorizonte insertAfericao(@NotNull final String tokenIntegracao,
                                                    @NotNull final Long codUnidade,
                                                    @NotNull final AfericaoRodoparHorizonte afericao) throws Throwable;

    @NotNull
    CronogramaAfericaoRodoparHorizonte getCronogramaAfericao(@NotNull final String tokenIntegracao,
                                                             @NotNull final Long codUnidade) throws Throwable;

    @NotNull
    NovaAfericaoPlacaRodoparHorizonte getNovaAfericaoPlaca(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUnidade,
            @NotNull final String placa,
            @NotNull final TipoMedicaoAfericaoRodoparHorizonte tipoAfericao) throws Throwable;
}
