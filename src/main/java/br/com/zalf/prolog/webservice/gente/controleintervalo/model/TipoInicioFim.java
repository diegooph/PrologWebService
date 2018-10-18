package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 08/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum TipoInicioFim {
    MARCACAO_INICIO("MARCACAO_INICIO") {
        @NotNull
        @Override
        public String getLegibleString() {
            return "Início";
        }
    },
    MARCACAO_FIM("MARCACAO_FIM") {
        @NotNull
        @Override
        public String getLegibleString() {
            return "Fim";
        }
    };

    @NotNull
    private final String tipoMarcacao;

    TipoInicioFim(@NotNull final String tipoMarcacao) {
        this.tipoMarcacao = tipoMarcacao;
    }

    @NotNull
    public abstract String getLegibleString();

    @NotNull
    public String asString() {
        return tipoMarcacao;
    }

    @NotNull
    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public static TipoInicioFim fromString(@NotNull final String tipoMarcacao) {
        Preconditions.checkNotNull(tipoMarcacao);

        for (final TipoInicioFim marcacao : TipoInicioFim.values()) {
            if (marcacao.tipoMarcacao.equals(tipoMarcacao)) {
                return marcacao;
            }
        }

        throw new IllegalArgumentException("Nenhum tipo de marcação encontrado com o nome: " + tipoMarcacao);
    }
}