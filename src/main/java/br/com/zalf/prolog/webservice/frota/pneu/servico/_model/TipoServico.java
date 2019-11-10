package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.components.charts.bar.BarColor;
import br.com.zalf.prolog.webservice.dashboard.components.charts.bar.BarEntryLegend;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created on 12/2/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum TipoServico implements BarEntryLegend, BarColor {
    CALIBRAGEM("calibragem") {
        @NotNull
        @Override
        public String getLegend() {
            return "Calibragens";
        }

        @NotNull
        @Override
        public Color getColor() {
            return Color.fromHex("#FED976");
        }
    },
    INSPECAO("inspecao") {
        @NotNull
        @Override
        public String getLegend() {
            return "Inspeções";
        }

        @NotNull
        @Override
        public Color getColor() {
            return Color.fromHex("#FD8D3C");
        }
    },
    MOVIMENTACAO("movimentacao") {
        @NotNull
        @Override
        public String getLegend() {
            return "Movimentações";
        }

        @NotNull
        @Override
        public Color getColor() {
            return Color.fromHex("#BD0026");
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