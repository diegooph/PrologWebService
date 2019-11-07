package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model;

import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.visualizacao.PneuTransferenciaProcessoVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.ProcessoTransferenciaVeiculoVisualizacao;
import org.jetbrains.annotations.NotNull;

/**
 * Objeto utilizado para vincular um {@link PneuTransferenciaProcessoVisualizacao processo de transferência de pneus} a
 * um {@link ProcessoTransferenciaVeiculoVisualizacao processo de transferência de veículos}.
 *
 * Created on 16/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class LinkTransferenciaVeiculo {
    /**
     * Código do processo de transferência de veículo.
     * {@link ProcessoTransferenciaVeiculoVisualizacao#codProcessoTransferencia}.
     */
    @NotNull
    private final Long codProcessoTransferenciaVeiculo;
    /**
     * Placa que foi transferida no processo de transferência e originou a transferência de pneus.
     */
    @NotNull
    private final String placaTransferencia;

    public LinkTransferenciaVeiculo(@NotNull final Long codProcessoTransferenciaVeiculo,
                                    @NotNull final String placaTransferencia) {
        this.codProcessoTransferenciaVeiculo = codProcessoTransferenciaVeiculo;
        this.placaTransferencia = placaTransferencia;
    }

    @NotNull
    public Long getCodProcessoTransferenciaVeiculo() {
        return codProcessoTransferenciaVeiculo;
    }

    @NotNull
    public String getPlacaTransferencia() {
        return placaTransferencia;
    }
}
