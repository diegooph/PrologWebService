package br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 09/10/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuMarcaBanda {

    @NotNull
    private final Long codigo;

    @NotNull
    private final String nome;

    @NotNull
    private final PneuModeloBandaVisualizacao modelo;

    public PneuMarcaBanda(@NotNull final Long codigo,
                          @NotNull final String nome,
                          @NotNull final PneuModeloBandaVisualizacao modelo) {
        this.codigo = codigo;
        this.nome = nome;
        this.modelo = modelo;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public PneuModeloBandaVisualizacao getModelo() {
        return modelo;
    }


    @Override
    public String toString() {
        return "Marca{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", modelos=" + modelo +
                '}';
    }
}
