package br.com.zalf.prolog.webservice.frota.checklist.offline;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum EstadoChecklistOfflineSupport {
    ATUALIZADO("ATUALIZADO"),
    ATUALIZACAO_FORCADA("ATUALIZACAO_FORCADA"),
    DESATUALIZADO("DESATUALIZADO"),
    SEM_DADOS("SEM_DADOS");

    @NotNull
    private final String estado;

    EstadoChecklistOfflineSupport(@NotNull final String estado) {
        this.estado = estado;
    }

    @NotNull
    public String asString() {
        return estado;
    }

    @Override
    public String toString() {
        return asString();
    }
}