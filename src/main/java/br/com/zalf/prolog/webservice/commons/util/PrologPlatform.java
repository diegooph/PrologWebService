package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/02/2020
 *
 * @author Wellington Vinícius (https://github.com/wvinim)
 */
public enum PrologPlatform {
    WEBSITE("WEBSITE"),
    ANDROID("ANDROID"),
    INTEGRACOES("INTEGRACOES");

    @NotNull
    private final String stringRepresentation;

    PrologPlatform(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }

    @Override
    public String toString() {
        return asString();
    }
}