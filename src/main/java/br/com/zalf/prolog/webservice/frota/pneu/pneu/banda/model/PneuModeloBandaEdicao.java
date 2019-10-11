package br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 18/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuModeloBandaEdicao {

    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codMarca;
    @NotNull
    private final String nome;
    private final int quantidadeSulcos;
    @NotNull
    private final Double alturaSulcos;

    public PneuModeloBandaEdicao(@NotNull final Long codEmpresa,
                                 @NotNull final Long codigo,
                                 @NotNull final Long codMarca,
                                 @NotNull final String nome,
                                 @NotNull final int quantidadeSulcos,
                                 @NotNull final Double alturaSulcos) {
        this.codEmpresa = codEmpresa;
        this.codigo = codigo;
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
    public Long getCodigo() {
        return codigo;
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