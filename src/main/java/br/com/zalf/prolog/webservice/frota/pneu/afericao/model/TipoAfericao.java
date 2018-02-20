package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.components.charts.bar.BarColor;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created on 10/11/17.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum TipoAfericao implements BarColor {
    SULCO("SULCO") {
        @Nonnull
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
        @Nonnull
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
        @Nonnull
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

    @Nonnull
    private final String stringRepresentation;

    TipoAfericao(@Nonnull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @Nonnull
    public abstract String getLegibleString();

    @Nonnull
    public String asString() {
        return stringRepresentation;
    }

    public static TipoAfericao fromString(@Nonnull final String string) {
        Preconditions.checkNotNull(string, "string cannot be null!");

        for (final TipoAfericao tipoAfericao : TipoAfericao.values()) {
            if (string.equals(tipoAfericao.stringRepresentation)) {
                return tipoAfericao;
            }
        }

        throw new IllegalArgumentException("Nenhum tipo de aferição encontrado para a string: " + string);
    }

    @Override
    public String toString() {
        return asString();
    }
}