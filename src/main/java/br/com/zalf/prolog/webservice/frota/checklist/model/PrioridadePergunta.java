package br.com.zalf.prolog.webservice.frota.checklist.model;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieSlice;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 21/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum PrioridadePergunta implements PieSlice {
    CRITICA("CRITICA") {
        @NotNull
        @Override
        public String getSliceDescription() {
            return "Crítica";
        }

        @NotNull
        @Override
        public Color getSliceColor() {
            return Color.fromHex("#E74C3C");
        }
    },
    ALTA("ALTA") {
        @NotNull
        @Override
        public String getSliceDescription() {
            return "Alta";
        }

        @NotNull
        @Override
        public Color getSliceColor() {
            return Color.fromHex("#F18E00");
        }
    },
    BAIXA("BAIXA") {
        @NotNull
        @Override
        public String getSliceDescription() {
            return "Baixa";
        }

        @NotNull
        @Override
        public Color getSliceColor() {
            return Color.fromHex("#F1C40F");
        }
    };

    @NotNull
    private final String stringRepresentation;

    PrioridadePergunta(@NotNull String stringRepresentation) {
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

    public static PrioridadePergunta fromString(String text) throws IllegalArgumentException {
        if (text != null) {
            for (final PrioridadePergunta prioridadePergunta : PrioridadePergunta.values()) {
                if (text.equalsIgnoreCase(prioridadePergunta.stringRepresentation)) {
                    return prioridadePergunta;
                }
            }
        }
        throw new IllegalArgumentException("Nenhuma prioridade encontrada para a String: " + text);
    }
}