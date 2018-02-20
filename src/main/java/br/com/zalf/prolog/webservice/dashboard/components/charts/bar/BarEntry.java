package br.com.zalf.prolog.webservice.dashboard.components.charts.bar;

import br.com.zalf.prolog.webservice.dashboard.base.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class BarEntry extends Entry {
    private double valor;
    @NotNull
    private String representacaoValor;
    private int index;
    @Nullable
    private String descricao;

    @NotNull
    public static BarEntry create(double valor,
                                  @NotNull String representacaoValor,
                                  int index,
                                  @Nullable String descricao) {
        return new BarEntry(valor, representacaoValor, index, descricao);
    }

    private BarEntry(double valor,
                    @NotNull String representacaoValor,
                    int index,
                    @Nullable String descricao) {
        this.valor = valor;
        this.representacaoValor = representacaoValor;
        this.index = index;
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
                "valor=" + valor +
                ", representacaoValor='" + representacaoValor + '\'' +
                ", index=" + index +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}