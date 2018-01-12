package br.com.zalf.prolog.webservice.commons.dashboard;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class BarEntry {

    @NotNull
    private String descricao;
    @NotNull
    private Double valor;
    @NotNull
    private Integer index;

    public BarEntry(@NotNull String descricao,
                    @NotNull Double valor,
                    @NotNull Integer index) {
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

    @NotNull
    public Double getValor() {
        return valor;
    }

    public void setValor(@NotNull Double valor) {
        this.valor = valor;
    }

    @NotNull
    public Integer getIndex() {
        return index;
    }

    public void setIndex(@NotNull Integer index) {
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
