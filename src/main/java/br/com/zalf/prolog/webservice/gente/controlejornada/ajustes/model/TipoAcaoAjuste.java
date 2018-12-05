package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Representa o tipo de ajuste que é possível de se fazer em uma marcação.
 *
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum TipoAcaoAjuste {
    /**
     * Constante para representar a adição de uma marcação, de início ou de fim, não ambas.
     */
    ADICAO("ADICAO"),

    /**
     * Constante para representar a edição de uma marcação.
     */
    EDICAO("EDICAO"),

    /**
     * Constante para representar a ativação de uma marcação.
     */
    ATIVACAO("ATIVACAO"),

    /**
     * Constante para representar a inativação de uma marcação.
     */
    INATIVACAO("INATIVACAO"),

    /**
     * Constante para representar a adição de uma marcação de início E fim, ao mesmo tempo.
     */
    ADICAO_INICIO_FIM("ADICAO_INICIO_FIM");

    @NotNull
    private final String stringRepresentation;

    TipoAcaoAjuste(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }

    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public static TipoAcaoAjuste fromString(@NotNull final String tipoMarcacaoAjuste) {
        Preconditions.checkNotNull(tipoMarcacaoAjuste, "tipoMarcacaoAjuste cannot be null!");

        for (final TipoAcaoAjuste value : values()) {
            if (tipoMarcacaoAjuste.equals(value.stringRepresentation)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Not found in this enum: " + tipoMarcacaoAjuste);
    }
}