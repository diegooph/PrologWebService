package br.com.zalf.prolog.webservice.integracao.sistema;

import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 18/07/17.
 */
public enum SistemaKey {
    AVACORP_AVILAN("AVACORP_AVILAN"),
    PROTHEUS_RODALOG("PROTHEUS_RODALOG");

    private final String key;

    SistemaKey(String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    public static SistemaKey fromString(@NotNull final String key) {
        Preconditions.checkNotNull(key, "key não pode ser nula!");

        final SistemaKey[] sistemaKeys = SistemaKey.values();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < sistemaKeys.length; i++) {
            if (sistemaKeys[i].key.equals(key)) {
                return sistemaKeys[i];
            }
        }

        throw new IllegalArgumentException("Nenhum sistema encontrado com a chave: " + key);
    }
}