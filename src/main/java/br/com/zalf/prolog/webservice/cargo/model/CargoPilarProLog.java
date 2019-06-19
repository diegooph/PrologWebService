package br.com.zalf.prolog.webservice.cargo.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by luiz on 4/18/16.
 */
public final class CargoPilarProLog {
    private final int codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final List<CargoFuncionalidadeProLog> funcionalidades;

    public CargoPilarProLog(final int codigo,
                            @NotNull final String nome,
                            @NotNull final List<CargoFuncionalidadeProLog> funcionalidades) {
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
    public List<CargoFuncionalidadeProLog> getFuncionalidades() {
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
