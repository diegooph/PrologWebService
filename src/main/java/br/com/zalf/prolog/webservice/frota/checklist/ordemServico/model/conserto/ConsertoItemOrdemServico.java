package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.conserto;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ConsertoItemOrdemServico {
    private Long codColaboradorFechamento;
    private Long codItemFechamento;
    private String feedbackFechamento;
    private String placaVeiculo;
    private long kmColetadoVeiculo;

    public ConsertoItemOrdemServico() {

    }

    public Long getCodColaboradorFechamento() {
        return codColaboradorFechamento;
    }

    public void setCodColaboradorFechamento(final Long codColaboradorFechamento) {
        this.codColaboradorFechamento = codColaboradorFechamento;
    }

    public Long getCodItemFechamento() {
        return codItemFechamento;
    }

    public void setCodItemFechamento(final Long codItemFechamento) {
        this.codItemFechamento = codItemFechamento;
    }

    public String getFeedbackFechamento() {
        return feedbackFechamento;
    }

    public void setFeedbackFechamento(final String feedbackFechamento) {
        this.feedbackFechamento = feedbackFechamento;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(final String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public long getKmColetadoVeiculo() {
        return kmColetadoVeiculo;
    }

    public void setKmColetadoVeiculo(final long kmColetadoVeiculo) {
        this.kmColetadoVeiculo = kmColetadoVeiculo;
    }
}