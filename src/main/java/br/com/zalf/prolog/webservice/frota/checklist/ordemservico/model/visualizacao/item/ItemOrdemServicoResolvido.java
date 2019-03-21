package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Esta é a classe utilizada para mostrar a Visualização de Itens Resolvidos de uma Ordem de Serviço.
 *
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ItemOrdemServicoResolvido extends ItemOrdemServicoVisualizacao {
    static final String TIPO_SERIALIZACAO = "ITEM_RESOLVIDO";

    private Long codColaboradorResolucao;
    private String nomeColaboradorResolucao;
    private LocalDateTime dataHoraResolvidoProLog;
    private String feedbackResolucao;
    /**
     * A data e hora em que a resolução do item foi iniciada pelo colaborador.
     */
    private LocalDateTime dataHoraInicioResolucao;

    /**
     * A data e hora em que a resolução do item foi finalizada pelo colaborador.
     */
    private LocalDateTime dataHoraFimResolucao;
    @SerializedName("duracaoResolucaoEmSegundos")
    private Duration duracaoResolucao;
    private long kmVeiculoColetadoResolucao;

    public ItemOrdemServicoResolvido() {
        super(TIPO_SERIALIZACAO);
    }

    @NotNull
    public static ItemOrdemServicoResolvido createDummy() {
        final ItemOrdemServicoResolvido item = new ItemOrdemServicoResolvido();
        item.setCodigo(1L);
        item.setCodOrdemServico(2L);
        item.setCodUnidadeItemOrdemServico(5L);
        item.setPergunta(PerguntaItemOrdemServico.createDummy());
        item.setDataHoraPrimeiroApontamento(LocalDateTime.now().minus(30, ChronoUnit.DAYS));
        item.setStatus(StatusItemOrdemServico.RESOLVIDO);
        item.setPrazoResolucaoItem(Duration.ofMinutes(42));
        item.setPrazoRestanteResolucaoItem(Duration.ofMinutes(20));
        item.setQtdApontamentos(10);

        // Resolvido.
        item.setCodColaboradorResolucao(10L);
        item.setNomeColaboradorResolucao("ProLoggerson");
        item.setDataHoraResolvidoProLog(LocalDateTime.now());
        item.setFeedbackResolucao("Feedback Fechamento");
        item.setDataHoraInicioResolucao(LocalDateTime.now().minusDays(2));
        item.setDataHoraFimResolucao(LocalDateTime.now().minusDays(1));
        item.setDuracaoResolucao(Duration.ofMinutes(10));
        item.setKmVeiculoColetadoResolucao(1000);
        return item;
    }

    public Long getCodColaboradorResolucao() {
        return codColaboradorResolucao;
    }

    public void setCodColaboradorResolucao(final Long codColaboradorResolucao) {
        this.codColaboradorResolucao = codColaboradorResolucao;
    }

    public String getNomeColaboradorResolucao() {
        return nomeColaboradorResolucao;
    }

    public void setNomeColaboradorResolucao(final String nomeColaboradorResolucao) {
        this.nomeColaboradorResolucao = nomeColaboradorResolucao;
    }

    public LocalDateTime getDataHoraResolvidoProLog() {
        return dataHoraResolvidoProLog;
    }

    public void setDataHoraResolvidoProLog(final LocalDateTime dataHoraResolvidoProLog) {
        this.dataHoraResolvidoProLog = dataHoraResolvidoProLog;
    }

    public String getFeedbackResolucao() {
        return feedbackResolucao;
    }

    public void setFeedbackResolucao(final String feedbackResolucao) {
        this.feedbackResolucao = feedbackResolucao;
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

    public Duration getDuracaoResolucao() {
        return duracaoResolucao;
    }

    public void setDuracaoResolucao(final Duration tempoResolucao) {
        this.duracaoResolucao = tempoResolucao;
    }

    public long getKmVeiculoColetadoResolucao() {
        return kmVeiculoColetadoResolucao;
    }

    public void setKmVeiculoColetadoResolucao(final long kmVeiculoColetadoResolucao) {
        this.kmVeiculoColetadoResolucao = kmVeiculoColetadoResolucao;
    }
}