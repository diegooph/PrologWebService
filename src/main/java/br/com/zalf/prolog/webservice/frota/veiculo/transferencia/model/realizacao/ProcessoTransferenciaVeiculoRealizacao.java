package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao;

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
     * Lista de códigos dos veículos que o colaborador está transferindo.
     */
    @NotNull
    private final List<Long> codVeiculosTransferencia;
    /**
     * Observação que o colaborador inseriu para este processo de transferência.
     */
    @Nullable
    private final String observacao;

    public ProcessoTransferenciaVeiculoRealizacao(@NotNull final Long codUnidadeOrigem,
                                                  @NotNull final Long codUnidadeDestino,
                                                  @NotNull final Long codColaboradorRealizacaoTransferencia,
                                                  @NotNull final List<Long> codVeiculosTransferencia,
                                                  @Nullable final String observacao) {
        this.codUnidadeOrigem = codUnidadeOrigem;
        this.codUnidadeDestino = codUnidadeDestino;
        this.codColaboradorRealizacaoTransferencia = codColaboradorRealizacaoTransferencia;
        this.codVeiculosTransferencia = codVeiculosTransferencia;
        this.observacao = observacao;
    }

    @NotNull
    public static ProcessoTransferenciaVeiculoRealizacao createDummy() {
        final List<Long> veiculosTransferencia = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            veiculosTransferencia.add((long) i);
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
    public List<Long> getCodVeiculosTransferencia() {
        return codVeiculosTransferencia;
    }

    @Nullable
    public String getObservacao() {
        return observacao;
    }
}