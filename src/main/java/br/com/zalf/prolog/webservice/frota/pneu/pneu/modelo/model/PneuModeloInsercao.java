package br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 26/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuModeloInsercao {
    @NotNull
    private Long codEmpresa;
    @NotNull
    private Long codMarca;
    @NotNull
    private String nome;

    private int quantidadeSulcos;
    @NotNull
    private Double alturaSulcos;

    public PneuModeloInsercao() {

    }

    public PneuModeloInsercao(@NotNull final Long codEmpresa,
                                   @NotNull final Long codMarca,
                                   @NotNull final String nome,
                                   final int quantidadeSulcos,
                                   @NotNull final Double alturaSulcos) {
        this.codEmpresa = codEmpresa;
        this.codMarca = codMarca;
        this.nome = nome;
        this.quantidadeSulcos = quantidadeSulcos;
        this.alturaSulcos = alturaSulcos;
    }

    public void setQuantidadeSulcos(int quantidadeSulcos) {
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    @NotNull
    public Long getCodMarca() {
        return codMarca;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public int getQuantidadeSulcos() {
        return quantidadeSulcos;
    }

    @NotNull
    public Double getAlturaSulcos() {
        return alturaSulcos;
    }
}
