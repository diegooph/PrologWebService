package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import br.com.zalf.prolog.webservice.entrega.produtividade.ItemProdutividade;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

/**
 * Created on 10/11/17.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum TipoAfericao {
    SULCO("SULCO"),
    PRESSAO("PRESSAO"),
    SULCO_PRESSAO("SULCO_PRESSAO");

    @Nonnull
    private final String stringRepresentation;

    TipoAfericao(@Nonnull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public static TipoAfericao fromString(@Nonnull final String string) {
        Preconditions.checkNotNull(string, "string cannot be null!");

        for (final TipoAfericao tipoAfericao : TipoAfericao.values()) {
            if (string.equals(tipoAfericao.stringRepresentation)) {
                return tipoAfericao;
            }
        }

        throw new IllegalArgumentException("Nenhum tipo de aferição encontrado para a string: " + string);
    }
}