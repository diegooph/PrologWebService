package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Esse objeto representa um veículo disponível para ser selecionado e transferido.
 *
 * Created on 2019-04-16
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class VeiculoSelecaoTransferencia {
    @NotNull
    private final Long codVeiculo;
    @NotNull
    private final String placaVeiculo;
    private final long kmAtualVeiculo;
    /**
     * Lista contendo os códigos dos pneus que estão aplicados na placa.
     */
    @NotNull
    private final List<Long> codPneusAplicadosVeiculo;
    private final int qtdPneusAplicados;

    public VeiculoSelecaoTransferencia(@NotNull final Long codVeiculo,
                                       @NotNull final String placaVeiculo,
                                       final long kmAtualVeiculo,
                                       @NotNull final List<Long> codPneusAplicadosVeiculo,
                                       final int qtdPneusAplicados) {
        this.codVeiculo = codVeiculo;
        this.placaVeiculo = placaVeiculo;
        this.kmAtualVeiculo = kmAtualVeiculo;
        this.codPneusAplicadosVeiculo = codPneusAplicadosVeiculo;
        this.qtdPneusAplicados = qtdPneusAplicados;
    }

    @NotNull
    public static VeiculoSelecaoTransferencia createDummy() {
        return new VeiculoSelecaoTransferencia(
                223L,
                "AAAA1111",
                220000,
                Lists.newArrayList(20L, 1L, 30L, 40L),
                10);
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

    @NotNull
    public List<Long> getCodPneusAplicadosVeiculo() {
        return codPneusAplicadosVeiculo;
    }

    public int getQtdPneusAplicados() {
        return qtdPneusAplicados;
    }
}