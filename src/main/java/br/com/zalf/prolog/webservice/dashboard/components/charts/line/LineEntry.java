package br.com.zalf.prolog.webservice.dashboard.components.charts.line;

import br.com.zalf.prolog.webservice.dashboard.base.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 18/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class LineEntry extends Entry {
    private final double valor;
    private final int index;
    @NotNull
    private final String representacaoValor;
    @Nullable
    private final String descricao;

    public LineEntry(final double valor,
                     final int index,
                     @NotNull final String representacaoValor,
                     @Nullable final String descricao) {
        this.valor = valor;
        this.index = index;
        this.representacaoValor = representacaoValor;
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public int getIndex() {
        return index;
    }

    @NotNull
    public String getRepresentacaoValor() {
        return representacaoValor;
    }

    @Nullable
    public String getDescricao() {
        return descricao;
    }
}