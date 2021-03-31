package br.com.zalf.prolog.webservice.frota.pneu._model;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieSlice;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Created on 1/22/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum StatusPneu implements PieSlice {
    EM_USO("EM_USO") {
        @NotNull
        @Override
        public PneuTipo toPneuTipo() {
            return PneuTipo.PNEU_EM_USO;
        }

        @NotNull
        @Override
        public String getSliceDescription() {
            return "Aplicados";
        }

        @NotNull
        @Override
        public Color getSliceColor() {
            return Color.fromHex("#1976D2");
        }

    },
    ESTOQUE("ESTOQUE") {
        @NotNull
        @Override
        public PneuTipo toPneuTipo() {
            return PneuTipo.PNEU_ESTOQUE;
        }

        @NotNull
        @Override
        public String getSliceDescription() {
            return "Em Estoque";
        }

        @NotNull
        @Override
        public Color getSliceColor() {
            return Color.fromHex("#26ac5f");
        }
    },
    DESCARTE("DESCARTE") {
        @NotNull
        @Override
        public PneuTipo toPneuTipo() {
            return PneuTipo.PNEU_DESCARTE;
        }

        @NotNull
        @Override
        public String getSliceDescription() {
            return "Descartados";
        }

        @NotNull
        @Override
        public Color getSliceColor() {
            return Color.fromHex("#c64234");
        }
    },
    ANALISE("ANALISE") {
        @NotNull
        @Override
        public PneuTipo toPneuTipo() {
            return PneuTipo.PNEU_ANALISE;
        }

        @NotNull
        @Override
        public String getSliceDescription() {
            return "Em Análise";
        }

        @NotNull
        @Override
        public Color getSliceColor() {
            return Color.fromHex("#cfa910");
        }
    };

    @NotNull
    private final String stringRepresentation;

    StatusPneu(@NotNull String stringRepresentation) {
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
    public abstract PneuTipo toPneuTipo();

    @NotNull
    public static StatusPneu fromString(@NotNull final String text) throws IllegalArgumentException {
        return Stream.of(StatusPneu.values())
                .filter(statusPneu -> statusPneu.stringRepresentation.equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum status encontrado para a String: " + text));
    }
}