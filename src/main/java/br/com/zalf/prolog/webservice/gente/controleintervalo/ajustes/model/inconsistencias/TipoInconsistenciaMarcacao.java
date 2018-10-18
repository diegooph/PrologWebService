package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.inconsistencias;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 18/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum  TipoInconsistenciaMarcacao {
    SEM_VINCULO("SEM_VINCULO"),
    INICIO_DEPOIS_FIM("INICIO_DEPOIS_FIM");

    @NotNull
    private final String stringRepresentation;

    TipoInconsistenciaMarcacao(@NotNull final String stringRepresentation) {
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
    public static TipoInconsistenciaMarcacao fromString(@NotNull final String tipoInconsistenciaMarcacao) {
        Preconditions.checkNotNull(tipoInconsistenciaMarcacao, "tipoInconsistenciaMarcacao cannot be null!");

        for (final TipoInconsistenciaMarcacao value : values()) {
            if (tipoInconsistenciaMarcacao.equals(value.stringRepresentation)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Not found in this enum: " + tipoInconsistenciaMarcacao);
    }
}