package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ProcessoTransferenciaVeiculo {
    @NotNull
    private final Long codUnidadeOrigem;
    @NotNull
    private final Long codUnidadeDestino;
    @NotNull
    private final Long codColaboradorRealizacaoTransferencia;
    @NotNull
    private final List<VeiculoTransferencia> veiculosTransferencia;
    @NotNull
    private final String observacao;

    public ProcessoTransferenciaVeiculo(@NotNull final Long codUnidadeOrigem,
                                        @NotNull final Long codUnidadeDestino,
                                        @NotNull final Long codColaboradorRealizacaoTransferencia,
                                        @NotNull final List<VeiculoTransferencia> veiculosTransferencia,
                                        @NotNull final String observacao) {
        this.codUnidadeOrigem = codUnidadeOrigem;
        this.codUnidadeDestino = codUnidadeDestino;
        this.codColaboradorRealizacaoTransferencia = codColaboradorRealizacaoTransferencia;
        this.veiculosTransferencia = veiculosTransferencia;
        this.observacao = observacao;
    }

    @NotNull
    public Long getCodUnidadeOrigem() {
        return codUnidadeOrigem;
    }

    @NotNull
    public Long getCodUnidadeDestino() {
        return codUnidadeDestino;
    }

    @NotNull
    public Long getCodColaboradorRealizacaoTransferencia() {
        return codColaboradorRealizacaoTransferencia;
    }

    @NotNull
    public List<VeiculoTransferencia> getVeiculosTransferencia() {
        return veiculosTransferencia;
    }

    @NotNull
    public String getObservacao() {
        return observacao;
    }
}