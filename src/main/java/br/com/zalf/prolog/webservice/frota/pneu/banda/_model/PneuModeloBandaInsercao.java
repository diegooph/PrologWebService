package br.com.zalf.prolog.webservice.frota.pneu.banda._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 25/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuModeloBandaInsercao {

    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codMarca;
    @NotNull
    private final String nome;
    private final int quantidadeSulcos;
    @NotNull
    private final Double alturaSulcos;

    public PneuModeloBandaInsercao(@NotNull final Long codEmpresa,
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
