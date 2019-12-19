package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 12/19/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class VeiculoAberturaSocorro {
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
     * Valor numérico que representa a quilometragem mais atual que temos desse veículo.
     */
    private final long kmAtualVeiculo;

    public VeiculoAberturaSocorro(@NotNull final Long codVeiculo,
                                  @NotNull final String placaVeiculo,
                                  final long kmAtualVeiculo){
        this.codVeiculo = codVeiculo;
        this.placaVeiculo = placaVeiculo;
        this.kmAtualVeiculo = kmAtualVeiculo;
    }

    @NotNull
    public Long getCodVeiculo() { return codVeiculo; }

    @NotNull
    public String getPlacaVeiculo() { return placaVeiculo; }

    public long getKmAtualVeiculo() { return kmAtualVeiculo; }
}