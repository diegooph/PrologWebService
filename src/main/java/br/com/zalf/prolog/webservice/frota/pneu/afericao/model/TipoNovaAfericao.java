package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

/**
 * Created on 27/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum TipoNovaAfericao {
    AFERICAO_PLACA("AFERICAO_PLACA"),
    AFERICAO_AVULSA("AFERICAO_PLACA");

    @Nonnull
    private final String stringRepresentation;

    TipoNovaAfericao(@Nonnull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @Nonnull
    public String asString() {
        return stringRepresentation;
    }

    public static TipoNovaAfericao fromString(@Nonnull final String string) {
        Preconditions.checkNotNull(string, "string cannot be null!");

        for (final TipoNovaAfericao tipoAfericao : TipoNovaAfericao.values()) {
            if (string.equals(tipoAfericao.stringRepresentation)) {
                return tipoAfericao;
            }
        }

        throw new IllegalArgumentException("Nenhum tipo de nova aferição encontrado para a string: " + string);
    }

    @Override
    public String toString() {
        return asString();
    }
}