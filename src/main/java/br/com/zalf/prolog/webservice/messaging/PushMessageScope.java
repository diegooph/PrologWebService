package br.com.zalf.prolog.webservice.messaging;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-02-03
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum PushMessageScope {
    ABERTURA_SOCORRO_ROTA("ABERTURA_SOCORRO_ROTA"),
    ATENDIMENTO_SOCORRO_ROTA("ATENDIMENTO_SOCORRO_ROTA"),
    INVALIDACAO_SOCORRO_ROTA("INVALIDACAO_SOCORRO_ROTA");

    @NotNull
    private final String stringRepresentation;

    PushMessageScope(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }
}
