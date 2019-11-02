package br.com.zalf.prolog.webservice.frota.pneu.modelo._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-11-02
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuMarcaListagem {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;

    public PneuMarcaListagem(@NotNull final Long codigo,
                             @NotNull final String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return "PneuMarcaListagem{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                '}';
    }
}
