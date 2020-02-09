package br.com.zalf.prolog.webservice.messaging;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum FirebasePlataformDestination {
    ANDROID("ANDROID");

    @NotNull
    private final String stringRepresentation;

    FirebasePlataformDestination(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }
}
