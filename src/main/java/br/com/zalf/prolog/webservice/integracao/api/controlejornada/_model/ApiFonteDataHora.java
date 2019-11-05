package br.com.zalf.prolog.webservice.integracao.api.controlejornada._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 02/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum ApiFonteDataHora {
    REDE_CELULAR("REDE_CELULAR"),
    LOCAL_CELULAR("LOCAL_CELULAR"),
    SERVIDOR("SERVIDOR");

    @NotNull
    private final String fonteDataHora;

    ApiFonteDataHora(@NotNull final String fonteDataHora) {
        this.fonteDataHora = fonteDataHora;
    }

    @NotNull
    public String asString() {
        return fonteDataHora;
    }

    @NotNull
    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public static ApiFonteDataHora fromString(@NotNull final String fonteDataHora) {
        for (final ApiFonteDataHora fonte : ApiFonteDataHora.values()) {
            if (fonte.fonteDataHora.equals(fonteDataHora)) {
                return fonte;
            }
        }

        throw new IllegalArgumentException("Nenhuma FonteDataHora encontrada com a chave: " + fonteDataHora);
    }
}
