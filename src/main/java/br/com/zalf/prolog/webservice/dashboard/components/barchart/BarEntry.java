package br.com.zalf.prolog.webservice.dashboard.components.barchart;

import br.com.zalf.prolog.webservice.dashboard.base.Entry;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class BarEntry extends Entry {

    @NotNull
    private String descricao;
    private double valor;
    @NotNull
    private String representacaoValor;
    private int index;

    public static BarEntry create(@NotNull final String descricao,
                                  final double valor,
                                  @NotNull final String representacaoValor,
                                  final int index) {
        return new BarEntry(descricao, valor, representacaoValor, index);
    }

    private BarEntry(@NotNull final String descricao,
                     final double valor,
                     @NotNull final String representacaoValor,
                     final int index) {
        this.descricao = descricao;
        this.valor = valor;
        this.representacaoValor = representacaoValor;
        this.index = index;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "BarEntry{" +
                "descricao='" + descricao + '\'' +
                ", valor=" + valor +
                ", representacaoValor='" + representacaoValor + '\'' +
                ", index=" + index +
                '}';
    }
}
