package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Classe que contém as informações do Conserto de um Item de uma Ordem de Serviço.
 *
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ResolverItemOrdemServico {
    /**
     * CPF do {@link Colaborador} que resolveu o Item.
     */
    private Long cpfColaboradoResolucao;

    /**
     * Código do Item que foi resolvido.
     */
    private Long codItemResolvido;

    /**
     * Descrição inserida pelo {@link Colaborador} no momento de resolução do Item.
     */
    private String feedbackResolucao;

    /**
     * Placa do {@link Veiculo} a qual o Item resolvido pertence.
     */
    private String placaVeiculo;

    /**
     * Quilometragem do {@link Veiculo} no momento de resolução do Item.
     */
    private long kmColetadoVeiculo;

    /**
     * Duração da resolução do Item.
     */
    @SerializedName("duracaoResolucaoItemEmSegundos")
    private Duration duracaoResolucaoItem;

    /**
     * Código da Ordem de Serviço a qual o Item pertence.
     */
    private Long codOrdemServico;

    /**
     * O código da {@link Unidade} da qual o item da Ordem de Serviço pertence.
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