package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 05/04/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ChecklistOfflineSupportComDados extends ChecklistOfflineSupport {
    @NotNull
    static final String COM_DADOS = "COM_DADOS";

    @NotNull
    private final ChecklistOfflineData checklistOfflineData;

    public ChecklistOfflineSupportComDados(@NotNull final Long codUnidade,
                                           @NotNull final EstadoChecklistOfflineSupport estadoChecklistOfflineSupport,
                                           @NotNull final ChecklistOfflineData checklistOfflineData,
                                           final boolean foiAtualizacaoForacada) {
        super(COM_DADOS, codUnidade, estadoChecklistOfflineSupport, foiAtualizacaoForacada);
        this.checklistOfflineData = checklistOfflineData;
    }

    @NotNull
    public ChecklistOfflineData getChecklistOfflineData() {
        return checklistOfflineData;
    }
}
