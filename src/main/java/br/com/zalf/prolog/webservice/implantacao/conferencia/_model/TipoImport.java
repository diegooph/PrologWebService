package br.com.zalf.prolog.webservice.implantacao.conferencia._model;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/12/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public enum TipoImport {

        VEICULO("VEICULO") {
            @NotNull
            @Override
            public String getLegibleString() {
                return "VEICULO";
            }
        },
        PNEU("PNEU") {
            @NotNull
            @Override
            public String getLegibleString() {
                return "PNEU";
            }
        },
        COLABORADOR("COLABORADOR") {
            @NotNull
            @Override
            public String getLegibleString() {
                return "COLABORADOR";
            }
        };

        @NotNull
        private final String stringRepresentation;

    TipoImport(@NotNull final String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        @NotNull
        public abstract String getLegibleString();

        @NotNull
        public String asString() {
            return stringRepresentation;
        }

        public static TipoImport fromString(@NotNull final String string) {
            Preconditions.checkNotNull(string, "string cannot be null!");

            for (final TipoImport tipoImport : TipoImport.values()) {
                if (string.equals(tipoImport.stringRepresentation)) {
                    return tipoImport;
                }
            }

            throw new IllegalArgumentException("Nenhum tipo de import encontrado para a string: " + string);
        }

        @Override
        public String toString() {
            return asString();
        }
}