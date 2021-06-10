package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum DeParaCamposPersonalizadosEnum {
    CODIGO_LIP_PNEU("codLipPneu"),
    CODIGO_ORIGEM_FILIAL("codOrigemFilial"),
    CODIGO_DESTINO_PNEU("codDestinoPneu"),
    CODIGO_CAUSA_SUCATA_PNEU("codCausaSucataPneu"),
    OBSERVACAO("observacao");

    @NotNull
    private final String stringRepresentation;

    DeParaCamposPersonalizadosEnum(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public static DeParaCamposPersonalizadosEnum fromString(@Nullable final String text)
            throws IllegalArgumentException {
        if (text != null) {
            for (final DeParaCamposPersonalizadosEnum deParaCamposPersonalizadosEnum
                    : DeParaCamposPersonalizadosEnum.values()) {
                if (text.equalsIgnoreCase(deParaCamposPersonalizadosEnum.asString())) {
                    return deParaCamposPersonalizadosEnum;
                }
            }
        }
        throw new IllegalArgumentException("Nenhum tipo de alteração encontrado para a String: " + text);
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
