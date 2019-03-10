package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistOfflineSupportSemDados extends ChecklistOfflineSupport {

    public ChecklistOfflineSupportSemDados(@NotNull final Long codUnidadeDados) {
        super(codUnidadeDados, EstadoChecklistOfflineSupport.SEM_DADOS);
    }
}