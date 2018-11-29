package br.com.zalf.prolog.webservice.seguranca.relato.model;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieSlice;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 28/11/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public enum StatusRelato implements PieSlice {
    PENDENTE_CLASSIFICACAO("PENDENTE_CLASSIFICACAO") {
        @NotNull
        @Override
        public String getSliceDescription() {
            return "Pendente Classificação";
        }

        @NotNull
        @Override
        public Color getSliceColor() {
            return Color.fromHex("#E74C3C");
        }
    },
    PENDENTE_FECHAMENTO("PENDENTE_FECHAMENTO") {
        @NotNull
        @Override
        public String getSliceDescription() {
            return "Pendente Fechamento";
        }

        @NotNull
        @Override
        public Color getSliceColor() {
            return Color.fromHex("#F18E00");
        }
    },
    FECHADO("FECHADO") {
        @NotNull
        @Override
        public String getSliceDescription() {
            return "Fechado";
        }

        @NotNull
        @Override
        public Color getSliceColor() {
            return Color.fromHex("#F1C40F");
        }
    },
    INVALIDO("INVALIDO") {
        @NotNull
        @Override
        public String getSliceDescription() {
            return "Inválido";
        }

        @NotNull
        @Override
        public Color getSliceColor() {
            return Color.fromHex("#C0C0C0");
        }
    };

    @NotNull
    private final String stringRepresentation;

    StatusRelato(@NotNull String stringRepresentation) {
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

    public static StatusRelato fromString(String text) throws IllegalArgumentException {
        if (text != null) {
            for (final StatusRelato statusRelato : StatusRelato.values()) {
                if (text.equalsIgnoreCase(statusRelato.stringRepresentation)) {
                    return statusRelato;
                }
            }
        }
        throw new IllegalArgumentException("Nenhum status encontrado para a String: " + text);
    }
}