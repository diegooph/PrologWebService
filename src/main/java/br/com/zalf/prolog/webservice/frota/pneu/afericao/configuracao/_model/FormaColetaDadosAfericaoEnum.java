package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Created on 2020-04-27
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public enum FormaColetaDadosAfericaoEnum {
    BLOQUEADO("BLOQUEADO"),
    EQUIPAMENTO("EQUIPAMENTO"),
    MANUAL("MANUAL"),
    EQUIPAMENTO_MANUAL("EQUIPAMENTO_MANUAL");

    @NotNull
    private final String stringRepresentation;

    FormaColetaDadosAfericaoEnum(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public static FormaColetaDadosAfericaoEnum fromString(@NotNull final String text) throws IllegalArgumentException {
        return Stream.of(FormaColetaDadosAfericaoEnum.values())
                .filter(e -> e.stringRepresentation.equals(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhuma forma de coleta dos dados encontrada " +
                                                                        "para a String: " + text));
    }

    @NotNull
    @Override
    public String toString() {
        return stringRepresentation;
    }
}
