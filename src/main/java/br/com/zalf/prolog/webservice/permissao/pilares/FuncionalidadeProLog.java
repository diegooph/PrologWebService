package br.com.zalf.prolog.webservice.permissao.pilares;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Wellington on 22/05/19.
 */
public class FuncionalidadeProLog {

    private final int codigo;

    @NotNull
    private final String nome;

    @NotNull
    private final List<PermissaoProLog> permissoes;

    public FuncionalidadeProLog(int codigo,
                                @NotNull String nome,
                                @NotNull List<PermissaoProLog> permissoes) {
        this.codigo = codigo;
        this.nome = nome;
        this.permissoes = permissoes;
    }

    public int getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public List<PermissaoProLog> getPermissoes() {
        return permissoes;
    }

    @Override
    public String toString() {
        return "FuncionalidadeProLog{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", permissoes=" + permissoes +
                '}';
    }
}
