package br.com.zalf.prolog.webservice.integracao.sistema;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Created by luiz on 18/07/17.
 */
public enum SistemaKey {
    AVACORP_AVILAN_OLD("AVACORP_AVILAN_OLD"),
    AVACORP_AVILAN("AVACORP_AVILAN"),
    TRANSPORT_TRANSLECCHI("TRANSPORT_TRANSLECCHI"),
    PROTHEUS_RODALOG("PROTHEUS_RODALOG"),
    PROTHEUS_NEPOMUCENO("PROTHEUS_NEPOMUCENO"),
    GLOBUS_PICCOLOTUR("GLOBUS_PICCOLOTUR"),
    RODOPAR_HORIZONTE("RODOPAR_HORIZONTE"),
    API_PROLOG("API_PROLOG");

    @NotNull
    private final String key;

    SistemaKey(@NotNull final String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
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