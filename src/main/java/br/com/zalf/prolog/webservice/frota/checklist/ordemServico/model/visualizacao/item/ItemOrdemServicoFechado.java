package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao.item;

import com.google.gson.annotations.SerializedName;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ItemOrdemServicoFechado extends ItemOrdemServicoVisualizacao {
    private Long codColaboradorFechamento;
    private String nomeColaboradorFechamento;
    private LocalDateTime dataHoraFechamento;
    private String feedbackFechamento;
    @SerializedName("tempoConsertoEmSegundos")
    private Duration tempoConserto;

    public ItemOrdemServicoFechado() {

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