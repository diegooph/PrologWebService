package br.com.zalf.prolog.webservice.frota.pneu.modelo._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 27/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuModeloListagem {
    @NotNull
    private final Long codMarcaBanda;
    @NotNull
    private final String nomeMarcaBanda;
    @NotNull
    private final Long codModeloBanda;
    @NotNull
    private final String nomeModeloBanda;
    private final int quantidadeSulcos;
    @NotNull
    private final Double alturaSulcos;

    public PneuModeloListagem(@NotNull final Long codMarcaBanda,
                              @NotNull final String nomeMarcaBanda,
                              @NotNull final Long codModeloBanda,
                              @NotNull final String nomeModeloBanda,
                              final int quantidadeSulcos,
                              @NotNull final Double alturaSulcos) {
        this.codMarcaBanda = codMarcaBanda;
        this.nomeMarcaBanda = nomeMarcaBanda;
        this.codModeloBanda = codModeloBanda;
        this.nomeModeloBanda = nomeModeloBanda;
        this.quantidadeSulcos = quantidadeSulcos;
        this.alturaSulcos = alturaSulcos;
    }

    @NotNull
    public Long getCodMarcaBanda() {
        return codMarcaBanda;
    }

    @NotNull
    public String getNomeMarcaBanda() {
        return nomeMarcaBanda;
    }

    @NotNull
    public Long getCodModeloBanda() {
        return codModeloBanda;
    }

    @NotNull
    public String getNomeModeloBanda() {
        return nomeModeloBanda;
    }

    public int getQuantidadeSulcos() {
        return quantidadeSulcos;
    }

    @NotNull
    public Double getAlturaSulcos() {
        return alturaSulcos;
    }
}