package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 3/11/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public enum TipoMedicaoAfericaoProtheusNepomuceno {
    SULCO("SULCO"),
    PRESSAO("PRESSAO"),
    SULCO_PRESSAO("SULCO_PRESSAO");

    @NotNull
    private final String stringRepresentation;

    TipoMedicaoAfericaoProtheusNepomuceno(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }

    public static TipoMedicaoAfericaoProtheusNepomuceno fromString(@NotNull final String tipo) {
        for (final TipoMedicaoAfericaoProtheusNepomuceno tipoMedicao : TipoMedicaoAfericaoProtheusNepomuceno.values()) {
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
