package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.inconsistencias;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Representa os tipos de inconsistências que uma marcação pode ter.
 *
 * Created on 18/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum TipoInconsistenciaMarcacao {
    /**
     * Constante para representar a inconsistência de marcações sem vínculo, onde marcações de início não tem fim ou
     * de fim não tem início.
     */
    SEM_VINCULO("SEM_VINCULO"),

    /**
     * Constante para representar a inconsistência onde uma marcação de fim tem data e hora anterior à marcação de
     * início da qual ela está vinculada.
     */
    FIM_ANTES_INICIO("FIM_ANTES_INICIO");

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