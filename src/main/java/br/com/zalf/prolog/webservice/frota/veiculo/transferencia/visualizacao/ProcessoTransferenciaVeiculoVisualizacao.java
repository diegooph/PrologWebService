package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.visualizacao;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ProcessoTransferenciaVeiculoVisualizacao {
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
    private final List<VeiculoTransferidoVisualizacao> veiculosTransferidos;
    private final int qtdVeiculosTransferidos;

    public ProcessoTransferenciaVeiculoVisualizacao(@NotNull final Long codProcessoTransferencia,
                                                    @NotNull final String nomeColaboradorRealizacao,
                                                    @NotNull final LocalDateTime dataHoraRealizacao,
                                                    @NotNull final String nomeUnidadeOrigem,
                                                    @NotNull final String nomeUnidadeDestino,
                                                    @NotNull final String nomeRegionalOrigem,
                                                    @NotNull final String nomeRegionalDestino,
                                                    @NotNull final String observacaoRealizacao,
                                                    @NotNull final List<VeiculoTransferidoVisualizacao> veiculosTransferidos,
                                                    final int qtdVeiculosTransferidos) {
        this.codProcessoTransferencia = codProcessoTransferencia;
        this.nomeColaboradorRealizacao = nomeColaboradorRealizacao;
        this.dataHoraRealizacao = dataHoraRealizacao;
        this.nomeUnidadeOrigem = nomeUnidadeOrigem;
        this.nomeUnidadeDestino = nomeUnidadeDestino;
        this.nomeRegionalOrigem = nomeRegionalOrigem;
        this.nomeRegionalDestino = nomeRegionalDestino;
        this.observacaoRealizacao = observacaoRealizacao;
        this.veiculosTransferidos = veiculosTransferidos;
        this.qtdVeiculosTransferidos = qtdVeiculosTransferidos;
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
    public List<VeiculoTransferidoVisualizacao> getVeiculosTransferidos() {
        return veiculosTransferidos;
    }

    public int getQtdVeiculosTransferidos() {
        return qtdVeiculosTransferidos;
    }
}