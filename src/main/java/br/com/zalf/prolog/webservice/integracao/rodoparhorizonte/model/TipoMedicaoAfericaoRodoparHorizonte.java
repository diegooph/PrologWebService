package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum TipoMedicaoAfericaoRodoparHorizonte {
    SULCO("SULCO"),
    PRESSAO("PRESSAO"),
    SULCO_PRESSAO("SULCO_PRESSAO");

    @NotNull
    private final String stringRepresentation;

    TipoMedicaoAfericaoRodoparHorizonte(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }

    public static TipoMedicaoAfericaoRodoparHorizonte fromString(@NotNull final String tipo) {
        for (final TipoMedicaoAfericaoRodoparHorizonte tipoMedicao : TipoMedicaoAfericaoRodoparHorizonte.values()) {
            if (tipo.equals(tipoMedicao.stringRepresentation)) {
                return tipoMedicao;
            }
        }

        throw new IllegalArgumentException("Nenhum tipo de aferição encontrado para a string: " + tipo);
    }

    @Override
    public String toString() {
        return asString();
    }
}
