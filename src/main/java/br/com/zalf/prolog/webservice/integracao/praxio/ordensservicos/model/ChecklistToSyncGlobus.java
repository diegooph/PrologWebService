package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 1/17/20
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ChecklistToSyncGlobus {
    @NotNull
    private final Long codModeloChecklist;
    @NotNull
    private final Long codVersaoModeloChecklist;
    @NotNull
    private final String placaVeiculoChecklist;
    @NotNull
    private final ChecklistItensNokGlobus checklistItensNokGlobus;

    public ChecklistToSyncGlobus(
            @NotNull final Long codModeloChecklist,
            @NotNull final Long codVersaoModeloChecklist,
            @NotNull final String placaVeiculoChecklist,
            @NotNull final ChecklistItensNokGlobus checklistItensNokGlobus) {
        this.codModeloChecklist = codModeloChecklist;
        this.codVersaoModeloChecklist = codVersaoModeloChecklist;
        this.placaVeiculoChecklist = placaVeiculoChecklist;
        this.checklistItensNokGlobus = checklistItensNokGlobus;
    }

    @NotNull
    public Long getCodModeloChecklist() {
        return codModeloChecklist;
    }

    @NotNull
    public Long getCodVersaoModeloChecklist() {
        return codVersaoModeloChecklist;
    }

    @NotNull
    public String getPlacaVeiculoChecklist() {
        return placaVeiculoChecklist;
    }

    @NotNull
    public ChecklistItensNokGlobus getChecklistItensNokGlobus() {
        return checklistItensNokGlobus;
    }
}
