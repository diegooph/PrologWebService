package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class VeiculoChecklistOffline {
    /**
     * Número único que identifica o veículo.
     */
    @NotNull
    private final Long codVeiculo;

    /**
     * Valor alfanumérico que representa a placa do veículo.
     */
    @NotNull
    private final String placaVeiculo;

    /**
     * Código de identificação do tipo de veículo a qual a {@code placaVeiculo placa} pertence.
     */
    @NotNull
    private final Long codTipoVeiculo;

    /**
     * Quilometragem que o veículo possui atualmente. Entendemos este valor como a quilometragem mais recente.
     */
    private final long kmAtualVeiculo;

    public VeiculoChecklistOffline(@NotNull final Long codVeiculo,
                                   @NotNull final String placaVeiculo,
                                   @NotNull final Long codTipoVeiculo,
                                   final long kmAtualVeiculo) {
        this.codVeiculo = codVeiculo;
        this.placaVeiculo = placaVeiculo;
        this.codTipoVeiculo = codTipoVeiculo;
        this.kmAtualVeiculo = kmAtualVeiculo;
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

    public long getKmAtualVeiculo() {
        return kmAtualVeiculo;
    }
}