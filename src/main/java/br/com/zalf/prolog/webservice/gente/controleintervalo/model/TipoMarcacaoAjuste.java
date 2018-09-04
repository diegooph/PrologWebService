package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum TipoMarcacaoAjuste {
    ADICAO("ADICAO"),
    EDICAO("EDICAO"),
    ATIVACAO_INATIVACAO("ATIVACAO_INATIVACAO"),
    ADICAO_INICIO_FIM("ADICAO_INICIO_FIM");

    @NotNull
    private final String stringRepresentation;

    TipoMarcacaoAjuste(@NotNull final String stringRepresentation) {
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

    public static TipoMarcacaoAjuste fromString(@NotNull final String tipoMarcacaoAjuste) {
        Preconditions.checkNotNull(tipoMarcacaoAjuste, "tipoMarcacaoAjuste cannot be null!");

        for (final TipoMarcacaoAjuste value : TipoMarcacaoAjuste.values()) {
            if (tipoMarcacaoAjuste.equals(value.stringRepresentation)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Not found in this enum: " + tipoMarcacaoAjuste);
    }
}
