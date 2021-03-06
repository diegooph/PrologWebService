package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TipoVeiculoChecklistOffline {
    /**
     * Número que representa o código do tipo de veículo. Identificador único para o tipo de veículo.
     */
    @NotNull
    private final Long codTipoVeiculo;

    public TipoVeiculoChecklistOffline(@NotNull final Long codTipoVeiculo) {
        this.codTipoVeiculo = codTipoVeiculo;
    }

    @NotNull
    public Long getCodTipoVeiculo() {
        return codTipoVeiculo;
    }
}