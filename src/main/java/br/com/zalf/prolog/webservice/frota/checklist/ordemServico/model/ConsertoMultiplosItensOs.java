package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model;

import com.google.gson.annotations.SerializedName;

import java.time.Duration;
import java.util.List;

public class ConsertoMultiplosItensOs {

    /**
     * Quem está realizando o conserto.
     */
    private Long codColaboradorConserto;

    /**
     * Duração do consertado.
     */
    @SerializedName("duracaoRealizacaoConsertoEmSegundos")
    private Duration duracaoRealizacaoConserto;

    /**
     * Km do veículo no momento em que o item foi fechado.
     */
    private long kmVeiculoFechamento;

    /**
     * Observação sobre o conserto.
     */
    private String feedbackResolucao;

    /**
     * Códigos dos {@link ItemOrdemServico itens} que serão fechados.
     */
    private List<Long> codigosItens;

    public ConsertoMultiplosItensOs() {

    }

    public Long getCodColaboradorConserto() {
        return codColaboradorConserto;
    }

    public void setCodColaboradorConserto(final Long codColaboradorConserto) {
        this.codColaboradorConserto = codColaboradorConserto;
    }

    public Duration getDuracaoRealizacaoConserto() {
        return duracaoRealizacaoConserto;
    }

    public void setDuracaoRealizacaoConserto(final Duration duracaoRealizacaoConserto) {
        this.duracaoRealizacaoConserto = duracaoRealizacaoConserto;
    }

    public long getKmVeiculoFechamento() {
        return kmVeiculoFechamento;
    }

    public void setKmVeiculoFechamento(final long kmVeiculoFechamento) {
        this.kmVeiculoFechamento = kmVeiculoFechamento;
    }

    public String getFeedbackResolucao() {
        return feedbackResolucao;
    }

    public void setFeedbackResolucao(final String feedbackResolucao) {
        this.feedbackResolucao = feedbackResolucao;
    }

    public List<Long> getCodigosItens() {
        return codigosItens;
    }

    public void setCodigosItens(final List<Long> codigosItens) {
        this.codigosItens = codigosItens;
    }
}