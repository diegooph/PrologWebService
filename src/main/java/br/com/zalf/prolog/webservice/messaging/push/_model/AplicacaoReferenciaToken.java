package br.com.zalf.prolog.webservice.messaging.push._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum AplicacaoReferenciaToken {
    PROLOG_ANDROID_DEBUG("PROLOG_ANDROID_DEBUG"),
    PROLOG_ANDROID_PROD("PROLOG_ANDROID_PROD"),
    PROLOG_WEB("PROLOG_WEB"),
    AFERE_FACIL_ANDROID_DEBUG("AFERE_FACIL_ANDROID_DEBUG"),
    AFERE_FACIL_ANDROID_PROD("AFERE_FACIL_ANDROID_PROD");

    @NotNull
    private final String stringRepresentation;

    AplicacaoReferenciaToken(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }
}
