package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data.RodoparHorizonteRequester;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class SistemaRodoparHorizonte extends Sistema {
    @NotNull
    private final RodoparHorizonteRequester requester;

    public SistemaRodoparHorizonte(@NotNull final RodoparHorizonteRequester requester,
                                   @NotNull final IntegradorProLog integradorProLog,
                                   @NotNull final SistemaKey sistemaKey,
                                   @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, userToken);
        this.requester = requester;
    }

    @Nullable
    @Override
    public Long insertAfericao(@NotNull final Long codUnidade, @NotNull final Afericao afericao) throws Throwable {
        return super.insertAfericao(codUnidade, afericao);
    }
}
