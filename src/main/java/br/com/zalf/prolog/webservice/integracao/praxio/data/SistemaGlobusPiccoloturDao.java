package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistItensNokGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistToSyncGlobus;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;

/**
 * Created on 17/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface SistemaGlobusPiccoloturDao {

    @NotNull
    ChecklistToSyncGlobus getChecklistToSyncGlobus(@NotNull final Connection conn,
                                                   @NotNull final Long codChecklistProLog) throws Throwable;

    void insertItensNokPendentesParaSincronizar(@NotNull final Connection conn,
                                                @NotNull final Long codChecklistParaSincronizar) throws Throwable;

    void insertItensNokEnviadosGlobus(
            @NotNull final Connection conn,
            @NotNull final ChecklistItensNokGlobus checklistItensNokGlobus) throws Throwable;

    void marcaChecklistNaoPrecisaSincronizar(@NotNull final Connection conn,
                                             @NotNull final Long codChecklistNaoPrecisaSincronizar) throws Throwable;

    void marcaChecklistSincronizado(@NotNull final Connection conn,
                                    @NotNull final Long codChecklistSincronizado) throws Throwable;

    void erroAoSicronizarChecklist(@NotNull final Connection conn,
                                   @NotNull final Long codChecklistProLog,
                                   @NotNull final String errorMessage,
                                   @NotNull final Throwable throwable) throws Throwable;

    boolean verificaItensIntegrados(@NotNull final List<Long> codItensResolver) throws Throwable;
}
