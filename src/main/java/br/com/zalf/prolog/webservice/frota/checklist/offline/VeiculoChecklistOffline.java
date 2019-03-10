package br.com.zalf.prolog.webservice.frota.checklist.offline;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class VeiculoChecklistOffline {
    @NotNull
    private final Long codVeiculo;
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final Long codTipoVeiculo;

    public VeiculoChecklistOffline(@NotNull final Long codVeiculo,
                                   @NotNull final String placaVeiculo,
                                   @NotNull final Long codTipoVeiculo) {
        this.codVeiculo = codVeiculo;
        this.placaVeiculo = placaVeiculo;
        this.codTipoVeiculo = codTipoVeiculo;
    }

    @NotNull
    public Long getCodVeiculo() {
        return codVeiculo;
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @NotNull
    public Long getCodTipoVeiculo() {
        return codTipoVeiculo;
    }
}