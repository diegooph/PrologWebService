package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 24/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PlacaItensOsAbertos {
    @NotNull
    private final String nomeUnidadePlaca;
    @NotNull
    private final String placa;
    private final int qtdItensOsAbertosPlaca;
    private final int qtdItensOsCriticosAbertosPlaca;

    public PlacaItensOsAbertos(@NotNull final String nomeUnidadePlaca,
                               @NotNull final String placa,
                               final int qtdItensOsAbertosPlaca,
                               final int qtdItensOsCriticosAbertosPlaca) {
        this.nomeUnidadePlaca = nomeUnidadePlaca;
        this.placa = placa;
        this.qtdItensOsAbertosPlaca = qtdItensOsAbertosPlaca;
        this.qtdItensOsCriticosAbertosPlaca = qtdItensOsCriticosAbertosPlaca;
    }

    @NotNull
    public String getNomeUnidadePlaca() {
        return nomeUnidadePlaca;
    }

    @NotNull
    public String getPlaca() {
        return placa;
    }

    public int getQtdItensOsAbertosPlaca() {
        return qtdItensOsAbertosPlaca;
    }

    public int getQtdItensOsCriticosAbertosPlaca() {
        return qtdItensOsCriticosAbertosPlaca;
    }
}