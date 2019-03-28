package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.frota.checklist.offline.ChecklistOfflineResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 27/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * Esta classe é utilizada para retornar o {@link EstadoChecklistOfflineSupport estado dos dados} do checklist para a
 * unidade solicitante.
 * <p>
 * A grande particularidade desta classe para a classe {@link ResponseChecklistWithCod} é que está retorna apenas o
 * estado dos dados.
 * @see ResponseChecklistWithCod
 * @see ChecklistOfflineResource#getEstadoDadosChecklistOffline(Long, Long);
 */
public class ResponseChecklist extends AbstractResponse {
    @Nullable
    private final EstadoChecklistOfflineSupport estadoChecklistOffline;

    private ResponseChecklist(@Nullable final EstadoChecklistOfflineSupport estadoChecklistOffline) {
        this.estadoChecklistOffline = estadoChecklistOffline;
    }

    @NotNull
    public static ResponseChecklist ok(@NotNull final String msg,
                                       @NotNull final EstadoChecklistOfflineSupport estadoChecklistOffline) {
        final ResponseChecklist responte = new ResponseChecklist(estadoChecklistOffline);
        responte.setStatus(OK);
        responte.setMsg(msg);
        return responte;
    }

    @NotNull
    public static ResponseChecklist error(@NotNull final String msg,
                                          @Nullable final EstadoChecklistOfflineSupport estadoChecklistOffline) {
        final ResponseChecklist responte = new ResponseChecklist(estadoChecklistOffline);
        responte.setStatus(ERROR);
        responte.setMsg(msg);
        return responte;
    }

    @Nullable
    public EstadoChecklistOfflineSupport getEstadoChecklistOffline() {
        return estadoChecklistOffline;
    }
}
