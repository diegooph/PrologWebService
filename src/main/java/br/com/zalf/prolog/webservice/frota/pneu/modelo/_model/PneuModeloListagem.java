package br.com.zalf.prolog.webservice.frota.pneu.modelo._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 27/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuModeloListagem {
    @NotNull
    private final Long codMarcaPneu;
    @NotNull
    private final String nomeMarcaPneu;
    @NotNull
    private final Long codModeloPneu;
    @NotNull
    private final String nomeModeloPneu;
    private final int quantidadeSulcos;
    @NotNull
    private final Double alturaSulcos;

    public PneuModeloListagem(@NotNull final Long codMarcaPneu,
                              @NotNull final String nomeMarcaPneu,
                              @NotNull final Long codModeloPneu,
                              @NotNull final String nomeModeloPneu,
                              final int quantidadeSulcos,
                              @NotNull final Double alturaSulcos) {
        this.codMarcaPneu = codMarcaPneu;
        this.nomeMarcaPneu = nomeMarcaPneu;
        this.codModeloPneu = codModeloPneu;
        this.nomeModeloPneu = nomeModeloPneu;
        this.quantidadeSulcos = quantidadeSulcos;
        this.alturaSulcos = alturaSulcos;
    }

    @NotNull
    public Long getCodMarcaPneu() {
        return codMarcaPneu;
    }

    @NotNull
    public String getNomeMarcaPneu() {
        return nomeMarcaPneu;
    }

    @NotNull
    public Long getCodModeloPneu() {
        return codModeloPneu;
    }

    @NotNull
    public String getNomeModeloPneu() {
        return nomeModeloPneu;
    }

    public int getQuantidadeSulcos() {
        return quantidadeSulcos;
    }

    @NotNull
    public Double getAlturaSulcos() {
        return alturaSulcos;
    }
}