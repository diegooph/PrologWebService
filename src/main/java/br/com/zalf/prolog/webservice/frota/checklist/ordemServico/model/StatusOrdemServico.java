package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum StatusOrdemServico {
    ABERTA("A"),
    FECHADA("F");

    @NotNull
    private final String status;

    StatusOrdemServico(@NotNull final String status) {
        this.status = status;
    }

    @NotNull
    public String asString() {
        return status;
    }

    @NotNull
    public static StatusOrdemServico fromString(@Nullable final String text) throws IllegalArgumentException {
        if (text != null) {
            for (StatusOrdemServico statusOs : StatusOrdemServico.values()) {
                if (text.equalsIgnoreCase(statusOs.status)) {
                    return statusOs;
                }
            }
        }

        throw new IllegalArgumentException("Nenhum enum com esse valor encontrado: " + text);
    }
}