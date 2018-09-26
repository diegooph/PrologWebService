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
    private final double y;
    private final double x;
    @NotNull
    private final String representacaoY;
    @NotNull
    private final String representacaoX;
    @Nullable
    private final String descricao;

    public LineEntry(final double y,
                     final double x,
                     @NotNull final String representacaoY,
                     @NotNull final String representacaoX,
                     @Nullable final String descricao) {
        this.y = y;
        this.x = x;
        this.representacaoY = representacaoY;
        this.representacaoX = representacaoX;
        this.descricao = descricao;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    @NotNull
    public String getRepresentacaoY() {
        return representacaoY;
    }

    @NotNull
    public String getRepresentacaoX() {
        return representacaoX;
    }

    @Nullable
    public String getDescricao() {
        return descricao;
    }
}