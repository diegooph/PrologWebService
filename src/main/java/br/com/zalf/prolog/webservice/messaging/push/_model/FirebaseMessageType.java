package br.com.zalf.prolog.webservice.messaging.push._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum FirebaseMessageType {
    MULTICAST("MULTICAST");

    @NotNull
    private final String stringRepresentation;

    FirebaseMessageType(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }
}
