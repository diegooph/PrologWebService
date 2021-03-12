package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.components.charts.bar.BarColor;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Created on 10/11/17.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum TipoMedicaoColetadaAfericao implements BarColor {
    SULCO("SULCO") {
        @NotNull
        @Override
        public String getLegibleString() {
            return "Sulco";
        }

        @NotNull
        @Override
        public Color getColor() {
            return Color.fromHex("#A8DDB5");
        }
    },
    PRESSAO("PRESSAO") {
        @NotNull
        @Override
        public String getLegibleString() {
            return "Pressão";
        }

        @NotNull
        @Override
        public Color getColor() {
            return Color.fromHex("#4EB3D3");
        }
    },
    SULCO_PRESSAO("SULCO_PRESSAO") {
        @NotNull
        @Override
        public String getLegibleString() {
            return "Sulco e Pressão";
        }

        @NotNull
        @Override
        public Color getColor() {
            return Color.fromHex("#08589E");
        }
    };

    @NotNull
    private final String stringRepresentation;

    TipoMedicaoColetadaAfericao(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public abstract String getLegibleString();

    @NotNull
    public String asString() {
        return stringRepresentation;
    }

    public static TipoMedicaoColetadaAfericao fromString(@NotNull final String string) {
        return Stream.of(TipoMedicaoColetadaAfericao.values())
                .filter(e -> e.stringRepresentation.equals(string))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum tipo de aferição encontrado para a string: "
                                                                        + string));
    }

    @Override
    public String toString() {
        return asString();
    }
}