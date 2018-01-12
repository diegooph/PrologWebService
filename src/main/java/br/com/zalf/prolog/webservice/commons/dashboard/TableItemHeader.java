package br.com.zalf.prolog.webservice.commons.dashboard;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TableItemHeader {

    @NotNull
    private String valor;
    @NotNull
    private String descricao;

    public TableItemHeader(@NotNull String valor, @NotNull String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    @NotNull
    public String getValor() {
        return valor;
    }

    public void setValor(@NotNull String valor) {
        this.valor = valor;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@NotNull String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "TableItemHeader{" +
                "valor='" + valor + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
