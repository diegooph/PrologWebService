package br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 29/07/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiPneuTransferencia {
    @NotNull
    private final Long codUnidadeOrigem;
    @NotNull
    private final Long codUnidadeDestino;
    @NotNull
    private final String cpfColaboradorRealizacaoTransferencia;
    @NotNull
    private final List<String> codPneusTransferidos;
    @Nullable
    private final String observacao;

    public ApiPneuTransferencia(@NotNull final Long codUnidadeOrigem,
                                @NotNull final Long codUnidadeDestino,
                                @NotNull final String cpfColaboradorRealizacaoTransferencia,
                                @NotNull final List<String> codPneusTransferidos,
                                @Nullable final String observacao) {
        this.codUnidadeOrigem = codUnidadeOrigem;
        this.codUnidadeDestino = codUnidadeDestino;
        this.cpfColaboradorRealizacaoTransferencia = cpfColaboradorRealizacaoTransferencia;
        this.codPneusTransferidos = codPneusTransferidos;
        this.observacao = observacao;
    }

    @NotNull
    public static ApiPneuTransferencia getPneuTransferenciaPraxioDummy() {
        return new ApiPneuTransferencia(
                5L,
                15L,
                "03383283104",
                Arrays.asList("2312", "3213"),
                "Pneus transferidos a partir do sistema Globus");
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
    public List<String> getCodPneusTransferidos() {
        return codPneusTransferidos;
    }

    @Nullable
    public String getObservacao() {
        return observacao;
    }
}
