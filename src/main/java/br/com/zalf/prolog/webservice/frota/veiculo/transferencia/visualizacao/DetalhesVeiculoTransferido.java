package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.visualizacao;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DetalhesVeiculoTransferido {
    /**
     * Atributo que identifica a placa do veículo.
     */
    @NotNull
    private final String placaVeiculo;
    /**
     * Código do diagrama que estava vinculado a placa quando ela foi transferida de Unidade.
     */
    @NotNull
    private final Long codDiagramaVeiculoMomentoTransferencia;
    /**
     * Nome do tipo de veículo estava vinculado a placa quando ela foi transferida de Unidade.
     */
    @NotNull
    private final String nomeTipoVeiculoMomentoTransferencia;
    /**
     * Lista contendo as informações dos pneus que foram transferidos junto com a placa.
     */
    @NotNull
    private final List<PneuVeiculoTransferido> pneusAplicadosMomentoTransferencia;

    public DetalhesVeiculoTransferido(@NotNull final String placaVeiculo,
                                      @NotNull final Long codDiagramaVeiculoMomentoTransferencia,
                                      @NotNull final String nomeTipoVeiculoMomentoTransferencia,
                                      @NotNull final List<PneuVeiculoTransferido> pneusAplicadosMomentoTransferencia) {
        this.placaVeiculo = placaVeiculo;
        this.codDiagramaVeiculoMomentoTransferencia = codDiagramaVeiculoMomentoTransferencia;
        this.nomeTipoVeiculoMomentoTransferencia = nomeTipoVeiculoMomentoTransferencia;
        this.pneusAplicadosMomentoTransferencia = pneusAplicadosMomentoTransferencia;
    }

    @NotNull
    public static DetalhesVeiculoTransferido createDummy() {
        final List<PneuVeiculoTransferido> pneusAplicados = new ArrayList<>();
        pneusAplicados.add(PneuVeiculoTransferido.createDummy());
        return new DetalhesVeiculoTransferido(
                "PRO0001",
                1L,
                "TOCO",
                pneusAplicados);
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @NotNull
    public Long getCodDiagramaVeiculoMomentoTransferencia() {
        return codDiagramaVeiculoMomentoTransferencia;
    }

    @NotNull
    public String getNomeTipoVeiculoMomentoTransferencia() {
        return nomeTipoVeiculoMomentoTransferencia;
    }

    @NotNull
    public List<PneuVeiculoTransferido> getPneusAplicadosMomentoTransferencia() {
        return pneusAplicadosMomentoTransferencia;
    }
}