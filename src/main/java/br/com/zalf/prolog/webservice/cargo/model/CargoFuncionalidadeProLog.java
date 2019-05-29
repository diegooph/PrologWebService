package br.com.zalf.prolog.webservice.cargo.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Wellington on 22/05/19.
 */
public final class CargoFuncionalidadeProLog {
    private final int codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final List<CargoPermissaoProLog> permissoes;

    public CargoFuncionalidadeProLog(final int codigo,
                                     @NotNull final String nome,
                                     @NotNull final List<CargoPermissaoProLog> permissoes) {
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
    public List<CargoPermissaoProLog> getPermissoes() {
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
