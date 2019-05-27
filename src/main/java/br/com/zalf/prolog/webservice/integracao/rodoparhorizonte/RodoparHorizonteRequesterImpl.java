package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte;

import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.AfericaoRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.CronogramaAfericaoRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.NovaAfericaoPlacaRodoparHorizonte;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.ResponseAfericaoRodoparHorizonte;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RodoparHorizonteRequesterImpl implements RodoparHorizonteRequester {

    @NotNull
    @Override
    public ResponseAfericaoRodoparHorizonte insertAfericao(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUnidade,
            @NotNull final AfericaoRodoparHorizonte afericao) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public CronogramaAfericaoRodoparHorizonte getCronogramaAfericao(@NotNull final String tokenIntegracao,
                                                                    @NotNull final Long codUnidade) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public NovaAfericaoPlacaRodoparHorizonte getNovaAfericaoPlaca(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUnidade,
            @NotNull final String placa,
            @NotNull final TipoMedicaoAfericaoRodoparHorizonte tipoAfericao) throws Throwable {
        return null;
    }
}
