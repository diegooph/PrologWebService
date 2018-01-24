package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import br.com.zalf.prolog.webservice.commons.dashboard.Color;
import br.com.zalf.prolog.webservice.commons.dashboard.base.PieSlice;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 1/22/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum StatusPneu implements PieSlice {
    EM_USO("EM_USO") {
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

    public static StatusPneu fromString(String text) throws IllegalArgumentException {
        if (text != null) {
            for (final StatusPneu statusPneu : StatusPneu.values()) {
                if (text.equalsIgnoreCase(statusPneu.stringRepresentation)) {
                    return statusPneu;
                }
            }
        }
        throw new IllegalArgumentException("Nenhum status encontrado para a String: " + text);
    }
}