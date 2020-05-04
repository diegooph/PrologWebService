package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-04-27
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public enum FormaColetaDadosAfericaoEnum {
    EQUIPAMENTO("EQUIPAMENTO"),
    MANUAL("MANUAL"),
    EQUIPAMENTO_MANUAL("EQUIPAMENTO_MANUAL");

    @NotNull
    private final String stringRepresentation;

    FormaColetaDadosAfericaoEnum(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public static FormaColetaDadosAfericaoEnum fromString(@Nullable final String text) throws IllegalArgumentException {
        if (text != null) {
            for (final FormaColetaDadosAfericaoEnum formaColetaDadosAfericaoEnum : FormaColetaDadosAfericaoEnum.values()) {
                if (text.equalsIgnoreCase(formaColetaDadosAfericaoEnum.toString())) {
                    return formaColetaDadosAfericaoEnum;
                }
            }
        }
        throw new IllegalArgumentException("Nenhuma forma de coleta dos dados encontrada para a String: " + text);
    }

    @NotNull
    @Override
    public String toString() {
        return stringRepresentation;
    }
}
