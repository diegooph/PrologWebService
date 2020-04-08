package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieSlice;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-12-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum StatusSocorroRota implements PieSlice {
    ABERTO("ABERTO") {
        @Override
        public @NotNull String getSliceDescription() {
            return "ABERTO";
        }

        @Override
        public @NotNull Color getSliceColor() {
            return Color.fromHex("#E74C3C");
        }
    },
    EM_ATENDIMENTO("EM_ATENDIMENTO") {
        @Override
        public @NotNull String getSliceDescription() {
            return "EM ATENDIMENTO";
        }

        @Override
        public @NotNull Color getSliceColor() {
            return Color.fromHex("#5579FF");
        }
    },
    INVALIDO("INVALIDO") {
        @Override
        public @NotNull String getSliceDescription() {
            return "INVÁLIDO";
        }

        @Override
        public @NotNull Color getSliceColor() {
            return Color.fromHex("#D2D2D2");
        }
    },
    FINALIZADO("FINALIZADO") {
        @Override
        public @NotNull String getSliceDescription() {
            return "FINALIZADO";
        }

        @Override
        public @NotNull Color getSliceColor() {
            return Color.fromHex("#2ECC71");
        }
    };

    @NotNull
    private final String stringRepresentation;

    StatusSocorroRota(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public static StatusSocorroRota fromString(final String text) throws IllegalArgumentException {
        if (text != null) {
            for (final StatusSocorroRota statusSocorroRota : StatusSocorroRota.values()) {
                if (text.equalsIgnoreCase(statusSocorroRota.stringRepresentation)) {
                    return statusSocorroRota;
                }
            }
        }
        throw new IllegalArgumentException("Nenhum status encontrado para a String: " + text);
    }

}
