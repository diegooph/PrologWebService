package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-08-17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class VeiculoChecklistSelecao {
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
     * Quilometragem que o veículo possui atualmente. Entendemos este valor como a quilometragem mais recente.
     */
    private final long kmAtualVeiculo;

    public VeiculoChecklistSelecao(@NotNull final Long codVeiculo,
                                   @NotNull final String placaVeiculo,
                                   final long kmAtualVeiculo) {
        this.codVeiculo = codVeiculo;
        this.placaVeiculo = placaVeiculo;
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

    public long getKmAtualVeiculo() {
        return kmAtualVeiculo;
    }
}
