package br.com.zalf.prolog.webservice.dashboard.components.charts.piechart;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.base.Entry;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PieEntry extends Entry {

    @NotNull
    private String descricao;
    private double valor;
    @NotNull
    private String representacaoValor;
    @NotNull
    private Color corFatia;

    public static PieEntry create(@NotNull final String descricao,
                                  final double valor,
                                  @NotNull final String representacaoValor,
                                  @NotNull final Color pieColor) {
        return new PieEntry(descricao, valor, representacaoValor, pieColor);
    }

    private PieEntry(@NotNull final String descricao,
                     final double valor,
                     @NotNull final String representacaoValor,
                     @NotNull final Color corFatia) {
        this.descricao = descricao;
        this.valor = valor;
        this.representacaoValor = representacaoValor;
        this.corFatia = corFatia;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@NotNull String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @NotNull
    public String getRepresentacaoValor() {
        return representacaoValor;
    }

    public void setRepresentacaoValor(@NotNull String representacaoValor) {
        this.representacaoValor = representacaoValor;
    }

    @NotNull
    public Color getCorFatia() {
        return corFatia;
    }

    public void setCorFatia(@NotNull Color corFatia) {
        this.corFatia = corFatia;
    }

    @Override
    public String toString() {
        return "PieEntry{" +
                "descricao='" + descricao + '\'' +
                ", valor=" + valor +
                ", representacaoValor='" + representacaoValor + '\'' +
                ", corFatia='" + corFatia + '\'' +
                '}';
    }
}
