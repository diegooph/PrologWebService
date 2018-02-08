package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

import br.com.zalf.prolog.webservice.dashboard.components.barchart.BarEntryLegend;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created on 12/2/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum TipoServico implements BarEntryLegend {
    CALIBRAGEM("calibragem") {
        @NotNull
        @Override
        public String getLegend() {
            return "Calibragens";
        }
    },
    INSPECAO("inspecao") {
        @NotNull
        @Override
        public String getLegend() {
            return "Inspeções";
        }
    },
    MOVIMENTACAO("movimentacao") {
        @NotNull
        @Override
        public String getLegend() {
            return "Movimentações";
        }
    };

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