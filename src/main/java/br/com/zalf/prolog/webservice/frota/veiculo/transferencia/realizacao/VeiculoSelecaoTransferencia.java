package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao;

import org.jetbrains.annotations.NotNull;

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
    private final int qtdPneusAplicados;

    public VeiculoSelecaoTransferencia(@NotNull final Long codVeiculo,
                                       @NotNull final String placaVeiculo,
                                       final long kmAtualVeiculo,
                                       final int qtdPneusAplicados) {
        this.codVeiculo = codVeiculo;
        this.placaVeiculo = placaVeiculo;
        this.kmAtualVeiculo = kmAtualVeiculo;
        this.qtdPneusAplicados = qtdPneusAplicados;
    }

    @NotNull
    public static VeiculoSelecaoTransferencia createDummy() {
        return new VeiculoSelecaoTransferencia(
                223L,
                "AAAA1111",
                220000,
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

    public int getQtdPneusAplicados() {
        return qtdPneusAplicados;
    }
}