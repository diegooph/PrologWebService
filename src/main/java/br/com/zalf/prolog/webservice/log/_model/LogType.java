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
    public static LogType fromApi(final boolean fromApi) {
        return fromApi ? LogType.FROM_API : LogType.FROM_PROLOG;
    }

    @NotNull
    public static LogType fromString(@NotNull final String key) {
        final LogType[] logTypes = LogType.values();
        for (final LogType logType : logTypes) {
            if (logType.key.equals(key)) {
                return logType;
            }
        }

        throw new IllegalArgumentException("Nenhum LogType encontrado com a chave: " + key);
    }

    @NotNull
    public String asString() {
        return key;
    }
}
