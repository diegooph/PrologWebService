package br.com.zalf.prolog.webservice.integracao.api.controlejornada.model;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 02/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum ApiTipoInicioFim {
    MARCACAO_INICIO("MARCACAO_INICIO"),
    MARCACAO_FIM("MARCACAO_FIM");

    @NotNull
    private final String tipoMarcacao;

    ApiTipoInicioFim(@NotNull final String tipoMarcacao) {
        this.tipoMarcacao = tipoMarcacao;
    }

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
    public static ApiTipoInicioFim fromString(@NotNull final String tipoMarcacao) {
        Preconditions.checkNotNull(tipoMarcacao);

        for (final ApiTipoInicioFim marcacao : ApiTipoInicioFim.values()) {
            if (marcacao.tipoMarcacao.equals(tipoMarcacao)) {
                return marcacao;
            }
        }

        throw new IllegalArgumentException("Nenhum tipo de marcação encontrado com o nome: " + tipoMarcacao);
    }
}
