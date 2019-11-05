package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 27/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum TipoProcessoColetaAfericao {
    PLACA("PLACA"),
    PNEU_AVULSO("PNEU_AVULSO");

    @NotNull
    private final String stringRepresentation;

    TipoProcessoColetaAfericao(@NotNull final String stringRepresentation) {
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

    public static TipoProcessoColetaAfericao fromString(@NotNull final String tipoProcessoAfericao) {
        Preconditions.checkNotNull(tipoProcessoAfericao, "tipoProcessoAfericao cannot be null!");

        for (final TipoProcessoColetaAfericao value : TipoProcessoColetaAfericao.values()) {
            if (tipoProcessoAfericao.equals(value.stringRepresentation)) {
                return value;
            }
        }

        throw new IllegalArgumentException("Not found in this enum: " + tipoProcessoAfericao);
    }
}