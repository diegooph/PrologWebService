package br.com.zalf.prolog.webservice.permissao.pilares;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Zalf on 24/10/16.
 */
public class PermissaoProLog {

    private final int codigo;

    @NotNull
    private final String descricao;

    public PermissaoProLog(int codigo, @NotNull String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return "PermissaoProLog{" +
                "codigo=" + codigo +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
