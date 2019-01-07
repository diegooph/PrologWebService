package br.com.zalf.prolog.webservice.integracao.transport;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created on 04/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ItemResolvidoIntegracaoTransport {
    private Long codUnidadeOrdemServico;
    private Long codOrdemServico;
    private Long codItemResolvido;
    private String cpfColaboradoResolucao;
    private Long kmColetadoVeiculo;
    private Duration duracaoResolucaoItem;
    private String feedbackResolucao;
    private LocalDateTime dataHoraResolucao;

    public ItemResolvidoIntegracaoTransport() {
    }

    public Long getCodUnidadeOrdemServico() {
        return codUnidadeOrdemServico;
    }

    public void setCodUnidadeOrdemServico(final Long codUnidadeOrdemServico) {
        this.codUnidadeOrdemServico = codUnidadeOrdemServico;
    }

    public Long getCodOrdemServico() {
        return codOrdemServico;
    }

    public void setCodOrdemServico(final Long codOrdemServico) {
        this.codOrdemServico = codOrdemServico;
    }

    public Long getCodItemResolvido() {
        return codItemResolvido;
    }

    public void setCodItemResolvido(final Long codItemResolvido) {
        this.codItemResolvido = codItemResolvido;
    }

    public String getCpfColaboradoResolucao() {
        return cpfColaboradoResolucao;
    }

    public void setCpfColaboradoResolucao(final String cpfColaboradoResolucao) {
        this.cpfColaboradoResolucao = cpfColaboradoResolucao;
    }

    public Long getKmColetadoVeiculo() {
        return kmColetadoVeiculo;
    }

    public void setKmColetadoVeiculo(final Long kmColetadoVeiculo) {
        this.kmColetadoVeiculo = kmColetadoVeiculo;
    }

    public Duration getDuracaoResolucaoItem() {
        return duracaoResolucaoItem;
    }

    public void setDuracaoResolucaoItem(final Duration duracaoResolucaoItem) {
        this.duracaoResolucaoItem = duracaoResolucaoItem;
    }

    public String getFeedbackResolucao() {
        return feedbackResolucao;
    }

    public void setFeedbackResolucao(final String feedbackResolucao) {
        this.feedbackResolucao = feedbackResolucao;
    }

    public LocalDateTime getDataHoraResolucao() {
        return dataHoraResolucao;
    }

    public void setDataHoraResolucao(final LocalDateTime dataHoraResolucao) {
        this.dataHoraResolucao = dataHoraResolucao;
    }
}
