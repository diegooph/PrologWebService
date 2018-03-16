package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 9/6/17.
 */

public enum FonteDataHora {
    REDE_CELULAR("REDE_CELULAR"),
    LOCAL_CELULAR("LOCAL_CELULAR"),
    SERVIDOR("SERVIDOR");

    private final String key;

    FonteDataHora(String key) {
        this.key = key;
    }

    @NotNull
    public String asString() {
        return key;
    }

    public static FonteDataHora fromString(@NotNull final String key) {
        Preconditions.checkNotNull(key);

        final FonteDataHora[] values = FonteDataHora.values();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < values.length; i++) {
            if (values[i].key.equals(key)) {
                return values[i];
            }
        }

        throw new IllegalArgumentException("Nenhuma FonteDataHora encontrada com a chave: " + key);
    }
}