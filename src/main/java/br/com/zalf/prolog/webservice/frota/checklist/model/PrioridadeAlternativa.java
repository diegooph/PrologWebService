package br.com.zalf.prolog.webservice.frota.checklist.model;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieSlice;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 21/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum PrioridadeAlternativa implements PieSlice {
    CRITICA("CRITICA") {

        @Override
        public int getPrazoResolucaoHoras() {
            return 1;
        }

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

        @Override
        public int getPrazoResolucaoHoras() {
            return 48;
        }

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

        @Override
        public int getPrazoResolucaoHoras() {
            return 720;
        }

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

    PrioridadeAlternativa(@NotNull String stringRepresentation) {
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

    public abstract int getPrazoResolucaoHoras();

    public static PrioridadeAlternativa fromString(String text) throws IllegalArgumentException {
        if (text != null) {
            for (final PrioridadeAlternativa prioridadeAlternativa : PrioridadeAlternativa.values()) {
                if (text.equalsIgnoreCase(prioridadeAlternativa.stringRepresentation)) {
                    return prioridadeAlternativa;
                }
            }
        }
        throw new IllegalArgumentException("Nenhuma prioridade encontrada para a String: " + text);
    }
}