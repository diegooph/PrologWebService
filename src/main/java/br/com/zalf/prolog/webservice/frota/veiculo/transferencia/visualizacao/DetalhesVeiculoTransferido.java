package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.visualizacao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DetalhesVeiculoTransferido {
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final Long codDiagramaVeiculo;
    @NotNull
    private final List<PneuVeiculoTransferido> pneusAplicadosMomentoTransferencia;

    public DetalhesVeiculoTransferido(@NotNull final String placaVeiculo,
                                      @NotNull final Long codDiagramaVeiculo,
                                      @NotNull final List<PneuVeiculoTransferido> pneusAplicadosMomentoTransferencia) {
        this.placaVeiculo = placaVeiculo;
        this.codDiagramaVeiculo = codDiagramaVeiculo;
        this.pneusAplicadosMomentoTransferencia = pneusAplicadosMomentoTransferencia;
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @NotNull
    public Long getCodDiagramaVeiculo() {
        return codDiagramaVeiculo;
    }

    @NotNull
    public List<PneuVeiculoTransferido> getPneusAplicadosMomentoTransferencia() {
        return pneusAplicadosMomentoTransferencia;
    }
}