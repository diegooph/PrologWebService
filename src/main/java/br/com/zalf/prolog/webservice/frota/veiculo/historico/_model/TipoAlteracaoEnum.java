package br.com.zalf.prolog.webservice.frota.veiculo.historico._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-09-15
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public enum TipoAlteracaoEnum {
    IDENTIFICADOR_FROTA("IDENTIFICADOR_FROTA") {
        @NotNull
        @Override
        public String getLegibleString() {
            return "IDENTIFICADOR DE FROTA";
        }
    },
    KM_VEICULO("KM_VEICULO") {
        @NotNull
        @Override
        public String getLegibleString() {
            return "KM DO VEICULO";
        }
    },
    STATUS_ATIVO("STATUS") {
        @NotNull
        @Override
        public String getLegibleString() {
            return "STATUS DO VEICULO";
        }
    },
    DIGRAMA_VEICULO("DIAGRAMA_VEICULO") {
        @NotNull
        @Override
        public String getLegibleString() {
            return "DIAGRAMA DO VEICULO";
        }
    },
    TIPO_VEICULO("TIPO_VEICULO") {
        @NotNull
        @Override
        public String getLegibleString() {
            return "TIPO DO VEICULO";
        }
    },
    MODELO_VEICULO("MODELO_VEICULO") {
        @NotNull
        @Override
        public String getLegibleString() {
            return "MODELO DO VEICULO";
        }
    },
    PLACA("PLACA") {
        @NotNull
        @Override
        public String getLegibleString() {
            return "PLACA DO VEICULO";
        }
    },
    MARCA_VEICULO("MARCA_VEICULO") {
        @NotNull
        @Override
        public String getLegibleString() {
            return "MARCA DO VEICULO";
        }
    };

    @NotNull
    private final String stringRepresentation;

    TipoAlteracaoEnum(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public static TipoAlteracaoEnum fromString(@Nullable final String text) throws IllegalArgumentException {
        if (text != null) {
            for (final TipoAlteracaoEnum tipoAlteracaoEnum : TipoAlteracaoEnum.values()) {
                if (text.equalsIgnoreCase(tipoAlteracaoEnum.asString())) {
                    return tipoAlteracaoEnum;
                }
            }
        }
        throw new IllegalArgumentException("Nenhum tipo de alteração encontrado para a String: " + text);
    }

    @NotNull
    public abstract String getLegibleString();

    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }
}
