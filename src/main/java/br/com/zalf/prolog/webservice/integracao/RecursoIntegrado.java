package br.com.zalf.prolog.webservice.integracao;

import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 7/18/17.
 */
public enum RecursoIntegrado {
    CHECKLIST("CHECKLIST"),
    VEICULOS("VEICULOS");

    @NotNull
    private final String key;

    RecursoIntegrado(String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    public static RecursoIntegrado fromString(@NotNull final String key) {
        Preconditions.checkNotNull(key, "key não pode ser nula!");

        final RecursoIntegrado[] recursosIntegrados = RecursoIntegrado.values();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < recursosIntegrados.length; i++) {
            if (recursosIntegrados[i].key.equals(key)) {
                return recursosIntegrados[i];
            }
        }

        throw new IllegalArgumentException("Nenhum recurso integrado encontrado com a chave: " + key);
    }
}