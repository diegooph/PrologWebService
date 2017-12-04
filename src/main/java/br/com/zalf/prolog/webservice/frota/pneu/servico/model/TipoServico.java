package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoAfericao;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

/**
 * Created on 12/2/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum TipoServico {
    CALIBRAGEM("calibragem"),
    INSPECAO("inspecao"),
    MOVIMENTACAO("movimentacao");

    private final String stringRepresentation;

    TipoServico(final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public String asString() {
        return stringRepresentation;
    }

    public static TipoServico fromString(@Nonnull final String string) {
        Preconditions.checkNotNull(string, "string cannot be null!");

        for (final TipoServico tipoServico : TipoServico.values()) {
            if (string.equals(tipoServico.stringRepresentation)) {
                return tipoServico;
            }
        }

        throw new IllegalArgumentException("Nenhum tipo de serviço encontrado para a string: " + string);
    }
}