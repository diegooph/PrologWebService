package br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuTransferenciaRealizacao {
    /**
     * Código da Unidade onde o pneu está alocado.
     */
    @NotNull
    private final Long codUnidadeOrigem;
    /**
     * Código da Unidade para onde o pneu será transferido.
     */
    @NotNull
    private final Long codUnidadeDestino;
    /**
     * Código do colaborador que está realizando a transferência dos pneus.
     */
    @NotNull
    private final Long codColaboradorRealizacaoTransferencia;
    /**
     * Código de identificação do pneu, normalmente, número de fogo do pneu.
     */
    @NotNull
    private final List<Long> codPneus;
    /**
     * Observação inserida pelo colaborador no momento da transferência. Este atributo pode estar vazio.
     */
    @Nullable
    private final String observacao;

    public PneuTransferenciaRealizacao(@NotNull final Long codUnidadeOrigem,
                                       @NotNull final Long codUnidadeDestino,
                                       @NotNull final Long codColaboradorRealizacaoTransferencia,
                                       @NotNull final List<Long> codPneus,
                                       @Nullable final String observacao) {
        this.codUnidadeOrigem = codUnidadeOrigem;
        this.codUnidadeDestino = codUnidadeDestino;
        this.codColaboradorRealizacaoTransferencia = codColaboradorRealizacaoTransferencia;
        this.codPneus = codPneus;
        this.observacao = observacao;
    }

    @NotNull
    public static PneuTransferenciaRealizacao createDummy() {
        final List<Long> codPneus = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            codPneus.add((long) i);
        }
        return new PneuTransferenciaRealizacao(
                5L,
                3L,
                190L,
                codPneus,
                "Operação de verão");
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
    public List<Long> getCodPneus() {
        return codPneus;
    }

    @Nullable
    public String getObservacao() {
        return observacao;
    }
}