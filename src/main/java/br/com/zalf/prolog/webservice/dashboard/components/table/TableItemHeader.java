package br.com.zalf.prolog.webservice.dashboard.components.table;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TableItemHeader {

    @NotNull
    private String nome;
    @Nullable
    private String descricao;

    public TableItemHeader(@NotNull String nome, @Nullable String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    public void setNome(@NotNull String nome) {
        this.nome = nome;
    }

    @Nullable
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@Nullable String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "TableItemHeader{" +
                "nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}