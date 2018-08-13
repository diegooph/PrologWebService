package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.NotNull;

import java.sql.Types;

/**
 * Created on 16/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum SqlType {
    BIGINT("bigint", Types.BIGINT),
    TEXT("text", Types.VARCHAR);

    @NotNull
    private final String typeString;
    private final int typeIntJava;

    SqlType(@NotNull final String typeString,
            final int typeIntJava) {
        this.typeString = typeString;
        this.typeIntJava = typeIntJava;
    }

    public int asIntTypeJava() {
        return typeIntJava;
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
