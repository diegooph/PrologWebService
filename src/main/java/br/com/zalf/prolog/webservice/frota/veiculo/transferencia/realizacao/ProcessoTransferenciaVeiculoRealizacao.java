package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ProcessoTransferenciaVeiculoRealizacao {
    /**
     * Código da Unidade onde o Veículo está alocado.
     */
    @NotNull
    private final Long codUnidadeOrigem;
    /**
     * Código da Unidade para onde o Veículo está sendo transferido.
     */
    @NotNull
    private final Long codUnidadeDestino;
    /**
     * Código do colaborador que está realizando o processo de transferência de veículos.
     */
    @NotNull
    private final Long codColaboradorRealizacaoTransferencia;
    /**
     * Lista de veículos que o colaborador está transferindo.
     */
    @NotNull
    private final List<VeiculoEnvioTransferencia> veiculosTransferencia;
    /**
     * Observação que o colaborador inseriu para este processo de transferência.
     */
    @Nullable
    private final String observacao;

    public ProcessoTransferenciaVeiculoRealizacao(@NotNull final Long codUnidadeOrigem,
                                                  @NotNull final Long codUnidadeDestino,
                                                  @NotNull final Long codColaboradorRealizacaoTransferencia,
                                                  @NotNull final List<VeiculoEnvioTransferencia> veiculosTransferencia,
                                                  @Nullable final String observacao) {
        this.codUnidadeOrigem = codUnidadeOrigem;
        this.codUnidadeDestino = codUnidadeDestino;
        this.codColaboradorRealizacaoTransferencia = codColaboradorRealizacaoTransferencia;
        this.veiculosTransferencia = veiculosTransferencia;
        this.observacao = observacao;
    }

    @NotNull
    public static ProcessoTransferenciaVeiculoRealizacao createDummy() {
        final List<VeiculoEnvioTransferencia> veiculosTransferencia = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            veiculosTransferencia.add(VeiculoEnvioTransferencia.createDummy());
        }
        return new ProcessoTransferenciaVeiculoRealizacao(
                10L,
                15L,
                1272L,
                veiculosTransferencia,
                "Estes veiculos estão sendo transferidos");
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
    public List<VeiculoEnvioTransferencia> getVeiculosTransferencia() {
        return veiculosTransferencia;
    }

    @Nullable
    public String getObservacao() {
        return observacao;
    }

    @NotNull
    public List<Long> getCodVeiculosTransferidos() {
        final List<Long> codVeiculosTransferidos = new ArrayList<>();
        for (final VeiculoEnvioTransferencia veiculoTransferencia : this.getVeiculosTransferencia()) {
            codVeiculosTransferidos.add(veiculoTransferencia.getCodVeiculo());
        }
        return codVeiculosTransferidos;
    }
}