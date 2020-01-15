package br.com.zalf.prolog.webservice.integracao.praxio.cadastro;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 05/07/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class VeiculoTransferenciaPraxio {
    @NotNull
    private final Long codUnidadeOrigem;
    @NotNull
    private final Long codUnidadeDestino;
    @NotNull
    private final String cpfColaboradorRealizacaoTransferencia;
    @NotNull
    private final String placaTransferida;
    @Nullable
    private final String observacao;

    public VeiculoTransferenciaPraxio(@NotNull final Long codUnidadeOrigem,
                                      @NotNull final Long codUnidadeDestino,
                                      @NotNull final String cpfColaboradorRealizacaoTransferencia,
                                      @NotNull final String placaTransferida,
                                      @Nullable final String observacao) {
        this.codUnidadeOrigem = codUnidadeOrigem;
        this.codUnidadeDestino = codUnidadeDestino;
        this.cpfColaboradorRealizacaoTransferencia = cpfColaboradorRealizacaoTransferencia;
        this.placaTransferida = placaTransferida;
        this.observacao = observacao;
    }

    @NotNull
    public static VeiculoTransferenciaPraxio getDummy() {
        return new VeiculoTransferenciaPraxio(
                5L,
                10L,
                "03383283194",
                "PRO0001",
                "teste");
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
    public String getCpfColaboradorRealizacaoTransferencia() {
        return cpfColaboradorRealizacaoTransferencia;
    }

    @NotNull
    public String getPlacaTransferida() {
        return placaTransferida;
    }

    @Nullable
    public String getObservacao() {
        return observacao;
    }
}
