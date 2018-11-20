package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao.item;

import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.StatusItemOrdemServico;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ItemOrdemServicoFechado extends ItemOrdemServicoVisualizacao {
    public static final String TIPO_SERIALIZACAO = "ITEM_FECHADO";

    private Long codColaboradorFechamento;
    private String nomeColaboradorFechamento;
    private LocalDateTime dataHoraFechamento;
    private String feedbackFechamento;
    @SerializedName("tempoConsertoEmSegundos")
    private Duration tempoConserto;

    public ItemOrdemServicoFechado() {
        super(TIPO_SERIALIZACAO);
    }

    @NotNull
    public static ItemOrdemServicoFechado createDummy() {
        final ItemOrdemServicoFechado item = new ItemOrdemServicoFechado();
        item.setCodigo(1L);
        item.setCodOrdemServico(2L);
        item.setCodUnidadeItemOrdemServico(5L);
        item.setPergunta(PerguntaItemOrdemServico.createDummy());
        item.setDataHoraPrimeiroApontamento(LocalDateTime.now().minus(30, ChronoUnit.DAYS));
        item.setStatus(StatusItemOrdemServico.PENDENTE);
        item.setPrazoConsertoItem(Duration.ofMinutes(42));
        item.setPrazoRestanteConsertoItem(Duration.ofMinutes(20));
        item.setQtdApontamentos(10);

        // Fechado.
        item.setCodColaboradorFechamento(10L);
        item.setNomeColaboradorFechamento("ProLoggerson");
        item.setDataHoraFechamento(LocalDateTime.now());
        item.setFeedbackFechamento("Feedback Fechamento");
        item.setTempoConserto(Duration.ofMinutes(10));
        return item;
    }

    public Long getCodColaboradorFechamento() {
        return codColaboradorFechamento;
    }

    public void setCodColaboradorFechamento(final Long codColaboradorFechamento) {
        this.codColaboradorFechamento = codColaboradorFechamento;
    }

    public String getNomeColaboradorFechamento() {
        return nomeColaboradorFechamento;
    }

    public void setNomeColaboradorFechamento(final String nomeColaboradorFechamento) {
        this.nomeColaboradorFechamento = nomeColaboradorFechamento;
    }

    public LocalDateTime getDataHoraFechamento() {
        return dataHoraFechamento;
    }

    public void setDataHoraFechamento(final LocalDateTime dataHoraFechamento) {
        this.dataHoraFechamento = dataHoraFechamento;
    }

    public String getFeedbackFechamento() {
        return feedbackFechamento;
    }

    public void setFeedbackFechamento(final String feedbackFechamento) {
        this.feedbackFechamento = feedbackFechamento;
    }

    public Duration getTempoConserto() {
        return tempoConserto;
    }

    public void setTempoConserto(final Duration tempoConserto) {
        this.tempoConserto = tempoConserto;
    }
}