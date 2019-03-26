package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 26/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ResponseChecklist extends ResponseWithCod {
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
    public static ResponseChecklist ok(@NotNull final Long codigo,
                                       @NotNull final String msg,
                                       @NotNull final EstadoChecklistOfflineSupport estadoChecklistOffline) {
        final ResponseChecklist responte = new ResponseChecklist(estadoChecklistOffline);
        responte.setCodigo(codigo);
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
