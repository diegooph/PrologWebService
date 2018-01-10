package br.com.zalf.prolog.webservice.commons.dashboard;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PieEntry {

    @NotNull
    private String descricao;
    private double valor;
    @NotNull
    private String pieColor;

    public PieEntry(@NotNull String descricao,
                    double valor,
                    @NotNull String pieColor) {
        this.descricao = descricao;
        this.valor = valor;
        this.pieColor = pieColor;
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
    public String getPieColor() {
        return pieColor;
    }

    public void setPieColor(@NotNull String pieColor) {
        this.pieColor = pieColor;
    }
}
