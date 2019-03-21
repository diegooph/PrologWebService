package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 04/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ItemResolvidoIntegracaoTransport {
    private Long codUnidadeOrdemServico;
    private Long codOrdemServico;
    private Long codItemResolvido;
    private String cpfColaboradoResolucao;
    /**
     * Placa do {@link Veiculo} a qual o item resolvido pertence.
     */
    private String placaVeiculo;
    private Long kmColetadoVeiculo;
    private Long duracaoResolucaoItemEmMilissegundos;
    private String feedbackResolucao;
    private LocalDateTime dataHoraResolvidoProLog;
    private LocalDateTime dataHoraInicioResolucao;
    private LocalDateTime dataHoraFimResolucao;

    public ItemResolvidoIntegracaoTransport() {
    }

    @NotNull
    public static ItemResolvidoIntegracaoTransport getDummy() {
        final ItemResolvidoIntegracaoTransport item = new ItemResolvidoIntegracaoTransport();
        item.setCodUnidadeOrdemServico(5L);
        item.setCodOrdemServico(94L);
        item.setCodItemResolvido(106851L);
        item.setCpfColaboradoResolucao("03383283194");
        item.setPlacaVeiculo("PRO0001");
        item.setKmColetadoVeiculo(90051L);
        item.setDuracaoResolucaoItemEmMilissegundos(900000L);
        item.setFeedbackResolucao("Item foi consertado.");
        item.setDataHoraResolvidoProLog(LocalDateTime.now());
        item.setDataHoraInicioResolucao(LocalDateTime.now());
        item.setDataHoraFimResolucao(LocalDateTime.now());
        return item;
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

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(final String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public Long getKmColetadoVeiculo() {
        return kmColetadoVeiculo;
    }

    public void setKmColetadoVeiculo(final Long kmColetadoVeiculo) {
        this.kmColetadoVeiculo = kmColetadoVeiculo;
    }

    public Long getDuracaoResolucaoItemEmMilissegundos() {
        return duracaoResolucaoItemEmMilissegundos;
    }

    public void setDuracaoResolucaoItemEmMilissegundos(final Long duracaoResolucaoItemEmMilissegundos) {
        this.duracaoResolucaoItemEmMilissegundos = duracaoResolucaoItemEmMilissegundos;
    }

    public String getFeedbackResolucao() {
        return feedbackResolucao;
    }

    public void setFeedbackResolucao(final String feedbackResolucao) {
        this.feedbackResolucao = feedbackResolucao;
    }

    public LocalDateTime getDataHoraResolvidoProLog() {
        return dataHoraResolvidoProLog;
    }

    public void setDataHoraResolvidoProLog(final LocalDateTime dataHoraResolvidoProLog) {
        this.dataHoraResolvidoProLog = dataHoraResolvidoProLog;
    }

    public LocalDateTime getDataHoraInicioResolucao() {
        return dataHoraInicioResolucao;
    }

    public void setDataHoraInicioResolucao(final LocalDateTime dataHoraInicioResolucao) {
        this.dataHoraInicioResolucao = dataHoraInicioResolucao;
    }

    public LocalDateTime getDataHoraFimResolucao() {
        return dataHoraFimResolucao;
    }

    public void setDataHoraFimResolucao(final LocalDateTime dataHoraFimResolucao) {
        this.dataHoraFimResolucao = dataHoraFimResolucao;
    }
}
