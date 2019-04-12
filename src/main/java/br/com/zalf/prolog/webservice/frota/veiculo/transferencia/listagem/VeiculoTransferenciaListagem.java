package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.listagem;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class VeiculoTransferenciaListagem {
    @NotNull
    private final Long codProcessoTransferencia;
    @NotNull
    private final String nomeColaboradorRealizacao;
    @NotNull
    private final LocalDateTime dataHoraRealizacao;
    @NotNull
    private final String nomeUnidadeOrigem;
    @NotNull
    private final String nomeUnidadeDestino;
    @NotNull
    private final String nomeRegionalOrigem;
    @NotNull
    private final String nomeRegionalDestino;
    @NotNull
    private final String observacaoRealizacao;
    @NotNull
    private final List<String> placasTransferidas;
    private final int qtdPlacasTransferidos;

    public VeiculoTransferenciaListagem(@NotNull final Long codProcessoTransferencia,
                                        @NotNull final String nomeColaboradorRealizacao,
                                        @NotNull final LocalDateTime dataHoraRealizacao,
                                        @NotNull final String nomeUnidadeOrigem,
                                        @NotNull final String nomeUnidadeDestino,
                                        @NotNull final String nomeRegionalOrigem,
                                        @NotNull final String nomeRegionalDestino,
                                        @NotNull final String observacaoRealizacao,
                                        @NotNull final List<String> placasTransferidas,
                                        final int qtdPlacasTransferidos) {
        this.codProcessoTransferencia = codProcessoTransferencia;
        this.nomeColaboradorRealizacao = nomeColaboradorRealizacao;
        this.dataHoraRealizacao = dataHoraRealizacao;
        this.nomeUnidadeOrigem = nomeUnidadeOrigem;
        this.nomeUnidadeDestino = nomeUnidadeDestino;
        this.nomeRegionalOrigem = nomeRegionalOrigem;
        this.nomeRegionalDestino = nomeRegionalDestino;
        this.observacaoRealizacao = observacaoRealizacao;
        this.placasTransferidas = placasTransferidas;
        this.qtdPlacasTransferidos = qtdPlacasTransferidos;
    }

    @NotNull
    public Long getCodProcessoTransferencia() {
        return codProcessoTransferencia;
    }

    @NotNull
    public String getNomeColaboradorRealizacao() {
        return nomeColaboradorRealizacao;
    }

    @NotNull
    public LocalDateTime getDataHoraRealizacao() {
        return dataHoraRealizacao;
    }

    @NotNull
    public String getNomeUnidadeOrigem() {
        return nomeUnidadeOrigem;
    }

    @NotNull
    public String getNomeUnidadeDestino() {
        return nomeUnidadeDestino;
    }

    @NotNull
    public String getNomeRegionalOrigem() {
        return nomeRegionalOrigem;
    }

    @NotNull
    public String getNomeRegionalDestino() {
        return nomeRegionalDestino;
    }

    @NotNull
    public String getObservacaoRealizacao() {
        return observacaoRealizacao;
    }

    @NotNull
    public List<String> getPlacasTransferidas() {
        return placasTransferidas;
    }

    public int getQtdPlacasTransferidos() {
        return qtdPlacasTransferidos;
    }
}