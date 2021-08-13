package br.com.zalf.prolog.webservice.interceptors.auth.authorization;

import org.jetbrains.annotations.NotNull;

public enum AuthType {
    BASIC("Basic"),
    BEARER("Bearer"),
    API("Api");

    @NotNull
    private final String value;

    AuthType(@NotNull final String value) {
        this.value = value;
    }

    @NotNull
    public String value() {
        return value;
    }
}