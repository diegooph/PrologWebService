package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model;

import javax.validation.constraints.NotNull;

/**
 * Created on 29/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ChecklistParaSincronizar {
    @NotNull
    private final Long codChecklist;
    private final boolean isLastCod;

    public ChecklistParaSincronizar(final Long codChecklist,
                                    final boolean isLastCod) {
        this.codChecklist = codChecklist;
        this.isLastCod = isLastCod;
    }

    public boolean temChecklistParaSincronizar() {
        return codChecklist > 0;
    }

    @NotNull
    public Long getCodChecklist() {
        return codChecklist;
    }

    public boolean isLastCod() {
        return isLastCod;
    }
}
