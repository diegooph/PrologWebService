package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.resolucao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ResolverItemOrdemServico {
    private Long codColaboradoResolucao;
    private Long codItemResolvido;
    private String feedbackResolucao;
    private String placaVeiculo;
    private long kmColetadoVeiculo;

    public ResolverItemOrdemServico() {

    }

    @NotNull
    public static ResolverItemOrdemServico createDummy() {
        final ResolverItemOrdemServico resolverItem = new ResolverItemOrdemServico();
        resolverItem.setCodColaboradoResolucao(123L);
        resolverItem.setCodItemResolvido(1L);
        resolverItem.setFeedbackResolucao("Resolvido!");
        resolverItem.setPlacaVeiculo("AAA1234");
        resolverItem.setKmColetadoVeiculo(1234L);
        return resolverItem;
    }

    public Long getCodColaboradoResolucao() {
        return codColaboradoResolucao;
    }

    public void setCodColaboradoResolucao(final Long codColaboradoResolucao) {
        this.codColaboradoResolucao = codColaboradoResolucao;
    }

    public Long getCodItemResolvido() {
        return codItemResolvido;
    }

    public void setCodItemResolvido(final Long codItemResolvido) {
        this.codItemResolvido = codItemResolvido;
    }

    public String getFeedbackResolucao() {
        return feedbackResolucao;
    }

    public void setFeedbackResolucao(final String feedbackResolucao) {
        this.feedbackResolucao = feedbackResolucao;
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