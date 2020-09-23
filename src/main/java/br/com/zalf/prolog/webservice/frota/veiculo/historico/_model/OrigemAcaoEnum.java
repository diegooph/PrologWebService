package br.com.zalf.prolog.webservice.frota.veiculo.historico._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-09-14
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public enum OrigemAcaoEnum {
    API("API"),
    PROLOG("PROLOG"),
    INTERNO("INTERNO"),
    SUPORTE("SUPORTE");

    @NotNull
    private final String stringRepresentation;

    OrigemAcaoEnum(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public static OrigemAcaoEnum fromString(@Nullable final String text) throws IllegalArgumentException {
        if (text != null) {
            for (final OrigemAcaoEnum origemAcaoEnum : OrigemAcaoEnum.values()) {
                if (text.equalsIgnoreCase(origemAcaoEnum.asString())) {
                    return origemAcaoEnum;
                }
            }
        }
        throw new IllegalArgumentException("Nenhuma origem encontrada para a String: " + text);
    }

    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }
}
