package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 01/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum TipoMedicaoAfericaoProtheusRodalog {
    SULCO("SULCO"),
    PRESSAO("PRESSAO"),
    SULCO_PRESSAO("SULCO_PRESSAO");

    @NotNull
    private final String stringRepresentation;

    TipoMedicaoAfericaoProtheusRodalog(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }

    public static TipoMedicaoAfericaoProtheusRodalog fromString(@NotNull final String tipo) {
        for (final TipoMedicaoAfericaoProtheusRodalog tipoMedicao : TipoMedicaoAfericaoProtheusRodalog.values()) {
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
