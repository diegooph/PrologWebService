package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.ChecklistOfflineResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * Created on 26/03/19.
 * <p>
 * Esta classe é utilizada para retornar o Código do Checklist que foi inserido através do processo de sincronização
 * de checklist offline.
 * <p>
 * Sempre que um checklist for sincronizado retornamos para a Aplicação o
 * {@link EstadoChecklistOfflineSupport estado dos dados} para a unidade da sincronização.
 * <p>
 * A grande particularidade desta classe para a classe {@link ResponseChecklist} é que está retorna, além do estado dos
 * dados, também o código do checklist que foi inserido.
 * @see ResponseChecklist
 * @see ChecklistOfflineResource#insert(String, long, long, ChecklistInsercao);
 */
public final class ResponseChecklistWithCod extends ResponseWithCod {
    @Nullable
    private final EstadoChecklistOfflineSupport estadoChecklistOffline;

    private ResponseChecklistWithCod(@Nullable final EstadoChecklistOfflineSupport estadoChecklistOffline) {
        this.estadoChecklistOffline = estadoChecklistOffline;
    }

    @NotNull
    public static ResponseChecklistWithCod ok(@NotNull final Long codigo,
                                              @NotNull final String msg,
                                              @NotNull final EstadoChecklistOfflineSupport estadoChecklistOffline) {
        final ResponseChecklistWithCod responte = new ResponseChecklistWithCod(estadoChecklistOffline);
        responte.setCodigo(codigo);
        responte.setStatus(OK);
        responte.setMsg(msg);
        return responte;
    }

    @NotNull
    public static ResponseChecklistWithCod error(@NotNull final String msg,
                                                 @Nullable final EstadoChecklistOfflineSupport estadoChecklistOffline) {
        final ResponseChecklistWithCod responte = new ResponseChecklistWithCod(estadoChecklistOffline);
        responte.setStatus(ERROR);
        responte.setMsg(msg);
        return responte;
    }

    @Nullable
    public EstadoChecklistOfflineSupport getEstadoChecklistOffline() {
        return estadoChecklistOffline;
    }
}
