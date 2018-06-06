package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 16/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum SqlType {
    BIGINT("bigint"),
    TEXT("text");

    @NotNull
    private final String typeString;

    SqlType(@NotNull final String typeString) {
        this.typeString = typeString;
    }

    @NotNull
    public String asString() {
        return typeString;
    }

    @Override
    public String toString() {
        return typeString;
    }
}
