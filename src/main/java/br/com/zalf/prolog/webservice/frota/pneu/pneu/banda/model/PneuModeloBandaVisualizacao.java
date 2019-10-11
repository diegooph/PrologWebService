package br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Created on 01/10/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuModeloBandaVisualizacao {

    @NotNull
    private Long codigo;

    @NotNull
    private String nome;

    private int quantidadeSulcos;

    @NotNull
    private Double alturaSulcos;

    private BigDecimal valorBanda;

    public PneuModeloBandaVisualizacao(@NotNull final Long codigo,
                                       @NotNull final String nome,
                                       final int quantidadeSulcos,
                                       @NotNull final Double alturaSulcos,
                                       final BigDecimal valorBanda) {
        this.codigo = codigo;
        this.nome = nome;
        this.quantidadeSulcos = quantidadeSulcos;
        this.alturaSulcos = alturaSulcos;
        this.valorBanda = valorBanda;
    }

    public PneuModeloBandaVisualizacao() {
    }

    public void setQuantidadeSulcos(final int quantidadeSulcos) {
        this.quantidadeSulcos = quantidadeSulcos;
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

    public void setCodigo(@NotNull final Long codModeloPneu) {
    }

    public BigDecimal getValorBanda() {
        return valorBanda;
    }

    public void setValorBanda(BigDecimal valorBanda) {
        this.valorBanda = valorBanda;
    }
}
