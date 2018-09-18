package br.com.zalf.prolog.webservice.dashboard.components.charts.line;

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

    public HorizontalLimitLine(final double posicaoY, @NotNull final String descricao) {
        this.posicaoY = posicaoY;
        this.descricao = descricao;
    }

    public double getPosicaoY() {
        return posicaoY;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }
}