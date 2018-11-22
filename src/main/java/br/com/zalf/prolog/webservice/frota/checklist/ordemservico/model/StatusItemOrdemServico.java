package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum StatusItemOrdemServico {
    RESOLVIDO("R"),
    PENDENTE("P");

    @NotNull
    private final String status;

    StatusItemOrdemServico(@NotNull final String status) {
        this.status = status;
    }

    @NotNull
    public String asString() {
        return status;
    }

    @NotNull
    public static StatusItemOrdemServico fromString(@Nullable final String text) throws IllegalArgumentException {
        if (text != null) {
            for (StatusItemOrdemServico statusItemOs : StatusItemOrdemServico.values()) {
                if (text.equalsIgnoreCase(statusItemOs.status)) {
                    return statusItemOs;
                }
            }
        }

        throw new IllegalArgumentException("Nenhum enum com esse valor encontrado: " + text);
    }
}