package br.com.zalf.prolog.webservice.raizen.produtividade.model;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created on 09/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public enum RaizenProdutividadeAgrupamento {
    POR_COLABORADOR("por-colaborador"),
    POR_DATA("por-data");

    @NotNull
    private final String identificador;

    RaizenProdutividadeAgrupamento(String identificador) {
        this.identificador = identificador;
    }

    @NotNull
    public String asString() {
        return identificador;
    }

    public static RaizenProdutividadeAgrupamento fromString(@Nonnull final String string) {
        Preconditions.checkNotNull(string, "string cannot be null!");

        for (final RaizenProdutividadeAgrupamento agrupamento : RaizenProdutividadeAgrupamento.values()) {
            if (string.equals(agrupamento.identificador)) {
                return agrupamento;
            }
        }
        throw new IllegalArgumentException("Nenhum tipo de componente encontrado para a string: " + string);
    }
}
