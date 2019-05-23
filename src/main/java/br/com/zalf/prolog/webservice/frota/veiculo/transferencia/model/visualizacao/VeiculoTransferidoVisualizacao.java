package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class VeiculoTransferidoVisualizacao {
    /**
     * Atributo que identifica o código do veículo.
     */
    @NotNull
    private final Long codVeiculo;
    /**
     * Atributo que identifica a placa do veículo.
     */
    @NotNull
    private final String placaVeiculo;
    /**
     * Nome do tipo de veículo que estava vinculado a placa quando ela foi transferida de Unidade.
     */
    @NotNull
    private final String nomeTipoVeiculoMomentoTransferencia;
    /**
     * Quilometragem do veículo no momento em que foi transferido.
     */
    private final long kmVeiculoMomentoTransferencia;
    /**
     * Uma Lista contendo a identificação de cada pneu para o cliente. Normalmente, o número de fogo do pneu.
     */
    @NotNull
    private final List<String> codPneusCliente;

    public VeiculoTransferidoVisualizacao(@NotNull final Long codVeiculo,
                                          @NotNull final String placaVeiculo,
                                          @NotNull final String nomeTipoVeiculoMomentoTransferencia,
                                          final long kmVeiculoMomentoTransferencia,
                                          @NotNull final List<String> codPneusCliente) {
        this.codVeiculo = codVeiculo;
        this.placaVeiculo = placaVeiculo;
        this.nomeTipoVeiculoMomentoTransferencia = nomeTipoVeiculoMomentoTransferencia;
        this.kmVeiculoMomentoTransferencia = kmVeiculoMomentoTransferencia;
        this.codPneusCliente = codPneusCliente;
    }

    @NotNull
    public static VeiculoTransferidoVisualizacao createDummy() {
        final List<String> codPneusCliente = new ArrayList<>();
        codPneusCliente.add("1092");
        codPneusCliente.add("1459");
        codPneusCliente.add("11102");
        return new VeiculoTransferidoVisualizacao(
                1L,
                "PRO0001",
                "TOCO",
                21212,
                codPneusCliente);
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @NotNull
    public String getNomeTipoVeiculoMomentoTransferencia() {
        return nomeTipoVeiculoMomentoTransferencia;
    }

    public long getKmVeiculoMomentoTransferencia() {
        return kmVeiculoMomentoTransferencia;
    }

    @NotNull
    public List<String> getCodPneusCliente() {
        return codPneusCliente;
    }
}