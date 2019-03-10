package br.com.zalf.prolog.webservice.frota.checklist.offline;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistOfflineSupportAtualizado extends ChecklistOfflineSupport {

    public ChecklistOfflineSupportAtualizado(@NotNull final Long codUnidadeDados) {
        super(codUnidadeDados, EstadoChecklistOfflineSupport.ATUALIZADO);
    }
}
