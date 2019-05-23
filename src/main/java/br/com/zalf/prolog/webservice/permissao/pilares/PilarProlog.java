package br.com.zalf.prolog.webservice.permissao.pilares;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by luiz on 4/18/16.
 */
public class PilarProlog {
    private final int codigo;

    @NotNull
    private final String nome;

    @NotNull
    private final List<FuncionalidadeProLog> funcionalidades;

    public PilarProlog(final int codigo,
                       @NotNull final String nome,
                       @NotNull final List<FuncionalidadeProLog> funcionalidades) {
        this.codigo = codigo;
        this.nome = nome;
        this.funcionalidades = funcionalidades;
    }

    public int getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public List<FuncionalidadeProLog> getFuncionalidades() {
        return funcionalidades;
    }

    @Override
    public String toString() {
        return "Pilar{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", funcionalidades=" + funcionalidades +
                '}';
    }
}
