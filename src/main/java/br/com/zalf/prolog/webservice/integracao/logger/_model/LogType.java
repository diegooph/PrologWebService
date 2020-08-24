package br.com.zalf.prolog.webservice.integracao.logger._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-08-24
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum LogType {
    FROM_API("FROM_API"),
    FROM_PROLOG("FROM_PROLOG");

    @NotNull
    private final String key;

    LogType(@NotNull final String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public static LogType fromString(@NotNull final String key) {
        final LogType[] recursosIntegrados = LogType.values();
        for (final LogType recursosIntegrado : recursosIntegrados) {
            if (recursosIntegrado.key.equals(key)) {
                return recursosIntegrado;
            }
        }

        throw new IllegalArgumentException("Nenhum recurso integrado encontrado com a chave: " + key);
    }
}
