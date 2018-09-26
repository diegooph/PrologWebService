package br.com.zalf.prolog.webservice.dashboard.components.charts.line;

import br.com.zalf.prolog.webservice.dashboard.Color;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 18/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class HorizontalLimitLine {
    private final double posicaoY;
    @NotNull
    private final String descricao;
    @NotNull
    private final Color cor;

    public HorizontalLimitLine(final double posicaoY, @NotNull final String descricao, @NotNull final Color cor) {
        this.posicaoY = posicaoY;
        this.descricao = descricao;
        this.cor = cor;
    }

    public double getPosicaoY() {
        return posicaoY;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    @NotNull
    public Color getCor() {
        return cor;
    }
}