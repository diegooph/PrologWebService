package br.com.zalf.prolog.webservice.commons.dashboard.components.barchart;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class BarEntry {

    @NotNull
    private String descricao;
    private double valor;
    private int index;

    public static BarEntry create(@NotNull final String descricao,
                                  final double valor,
                                  final int index) {
        return new BarEntry(descricao, valor, index);
    }

    private BarEntry(@NotNull String descricao,
                     double valor,
                     int index) {
        this.descricao = descricao;
        this.valor = valor;
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
                ", index=" + index +
                '}';
    }
}
