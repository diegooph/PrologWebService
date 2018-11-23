package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ResolverItemOrdemServico {
    private Long cpfColaboradoResolucao;
    private Long codItemResolvido;
    private String feedbackResolucao;
    private String placaVeiculo;
    private long kmColetadoVeiculo;

    /**
     * Duração da resolução dos itens.
     */
    @SerializedName("duracaoResolucaoItemEmSegundos")
    private Duration duracaoResolucaoItem;

    private Long codOrdemServico;

    /**
     * O código da unidade da qual os itens de O.S. pertencem.
     */
    private Long codUnidadeOrdemServico;

    public ResolverItemOrdemServico() {

    }

    @NotNull
    public static ResolverItemOrdemServico createDummy() {
        final ResolverItemOrdemServico resolverItem = new ResolverItemOrdemServico();
        resolverItem.setCpfColaboradoResolucao(12345678987L);
        resolverItem.setCodItemResolvido(1L);
        resolverItem.setFeedbackResolucao("Resolvido!");
        resolverItem.setPlacaVeiculo("AAA1234");
        resolverItem.setKmColetadoVeiculo(1234L);
        resolverItem.setDuracaoResolucaoItem(Duration.ofMinutes(10));
        resolverItem.setCodOrdemServico(10L);
        resolverItem.setCodUnidadeOrdemServico(5L);
        return resolverItem;
    }

    public Long getCpfColaboradoResolucao() {
        return cpfColaboradoResolucao;
    }

    public void setCpfColaboradoResolucao(final Long cpfColaboradoResolucao) {
        this.cpfColaboradoResolucao = cpfColaboradoResolucao;
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

    public Duration getDuracaoResolucaoItem() {
        return duracaoResolucaoItem;
    }

    public void setDuracaoResolucaoItem(final Duration duracaoResolucaoItem) {
        this.duracaoResolucaoItem = duracaoResolucaoItem;
    }

    public Long getCodOrdemServico() {
        return codOrdemServico;
    }

    public void setCodOrdemServico(final Long codOrdemServico) {
        this.codOrdemServico = codOrdemServico;
    }

    public Long getCodUnidadeOrdemServico() {
        return codUnidadeOrdemServico;
    }

    public void setCodUnidadeOrdemServico(final Long codUnidadeOrdemServico) {
        this.codUnidadeOrdemServico = codUnidadeOrdemServico;
    }
}