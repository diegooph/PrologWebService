package br.com.zalf.prolog.webservice.raizen.produtividade.model.itens;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 23/07/2018.
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum RaizenProdutividadeItemTipo {
    ITEM_COLABORADOR("ITEM_COLABORADOR"),
    ITEM_DATA("ITEM_DATA");

    @NotNull
    private final String identificador;

    RaizenProdutividadeItemTipo(@NotNull String identificador) {
        this.identificador = identificador;
    }

    @NotNull
    public String asString() {
        return identificador;
    }

    public static RaizenProdutividadeItemTipo fromString(@NotNull final String string) {
        Preconditions.checkNotNull(string, "string cannot be null");

        for (final RaizenProdutividadeItemTipo agrupamento : RaizenProdutividadeItemTipo.values()) {
            if (string.equals(agrupamento.identificador)) {
                return agrupamento;
            }
        }
        throw new IllegalArgumentException("Nenhum tipo de coponente encontrado para a string: " + string);
    }
}
