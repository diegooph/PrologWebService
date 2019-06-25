package br.com.zalf.prolog.webservice.cargo.model;

import br.com.zalf.prolog.webservice.permissao.pilares.ImpactoPermissaoProLog;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Zalf on 24/10/16.
 */
public final class CargoPermissaoProLog {
    private final int codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final ImpactoPermissaoProLog impacto;
    @NotNull
    private final String descricao;
    private final boolean permissaoLiberada;

    public CargoPermissaoProLog(final int codigo,
                                @NotNull final String nome,
                                @NotNull final ImpactoPermissaoProLog impacto,
                                @NotNull final String descricao,
                                final boolean permissaoLiberada) {
        this.codigo = codigo;
        this.nome = nome;
        this.impacto = impacto;
        this.descricao = descricao;
        this.permissaoLiberada = permissaoLiberada;
    }

    public int getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public ImpactoPermissaoProLog getImpacto() {
        return impacto;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    public boolean isPermissaoLiberada() {
        return permissaoLiberada;
    }

    @Override
    public String toString() {
        return "PermissaoProLog{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", impacto=" + impacto +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
