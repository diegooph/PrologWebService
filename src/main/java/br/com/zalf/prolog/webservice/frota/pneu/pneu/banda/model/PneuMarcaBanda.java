package br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 25/10/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuMarcaBanda {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;

    public PneuMarcaBanda(@NotNull final Long codigo,
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
