package br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 01/10/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuModeloBandaVisualizacao {

    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    private final int quantidadeSulcos;
    @NotNull
    private final Double alturaSulcos;

    public PneuModeloBandaVisualizacao(@NotNull final Long codigo,
                                       @NotNull final String nome,
                                       final int quantidadeSulcos,
                                       @NotNull final Double alturaSulcos) {
        this.codigo = codigo;
        this.nome = nome;
        this.quantidadeSulcos = quantidadeSulcos;
        this.alturaSulcos = alturaSulcos;
    }

    public Long getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidadeSulcos() {
        return quantidadeSulcos;
    }

    public Double getAlturaSulcos() {
        return alturaSulcos;
    }
}
