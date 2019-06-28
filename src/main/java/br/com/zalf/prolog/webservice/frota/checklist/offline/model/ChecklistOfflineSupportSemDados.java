package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 05/04/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ChecklistOfflineSupportSemDados extends ChecklistOfflineSupport {
    @NotNull
    static final String SEM_DADOS = "SEM_DADOS";

    public ChecklistOfflineSupportSemDados(@NotNull final Long codUnidade,
                                           @NotNull final EstadoChecklistOfflineSupport estadoChecklistOfflineSupport,
                                           final boolean foiAtualizacaoForacada) {
        super(SEM_DADOS, codUnidade, estadoChecklistOfflineSupport, foiAtualizacaoForacada);
    }
}
