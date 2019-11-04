package br.com.zalf.prolog.webservice.frota.pneu.banda._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-11-04
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuMarcaBandaVisualizacao {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;

    public PneuMarcaBandaVisualizacao(@NotNull final Long codigo,
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
        return "Marca{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                '}';
    }
}
