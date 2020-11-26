package br.com.zalf.prolog.webservice.integracao.avacorpavilan._model;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-11-07
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum IntegracaoOsFilter {
    ABERTAS(StatusOrdemServico.ABERTA.asString()),
    FECHADAS(StatusOrdemServico.FECHADA.asString()),
    TODAS(null);

    @Nullable
    private final String status;

    IntegracaoOsFilter(@Nullable final String status) {
        this.status = status;
    }

    @Nullable
    public String asString() {
        return this.status;
    }
}
