package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilizada para a resolução de múltiplos Itens de uma mesma Ordem de Serviço.
 *
 * Created on 20/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ResolverMultiplosItensOs {
    /**
     * CPF do {@link Colaborador} que resolveu os Itens.
     */
    private Long cpfColaboradorResolucao;

    /**
     * Placa do {@link Veiculo} a qual os Itens resolvidos pertencem.
     */
    private String placaVeiculo;

    /**
     * Duração da resolução dos itens.
     */
    @SerializedName("duracaoResolucaoItensEmSegundos")
    private Duration duracaoResolucaoItens;

    /**
     * Quilometragem do {@link Veiculo} no momento de resolução dos Itens.
     */
    private long kmColetadoVeiculo;

    /**
     * Descrição inserida pelo {@link Colaborador} no momento de resolução dos Itens.
     */
    private String feedbackResolucao;

    /**
     * O código da {@link Unidade} da qual os itens da Ordem de Serviço pertencem.
     */
    private Long codUnidadeOrdemServico;

    /**
     * Códigos dos itens que foram resolvidos.
     */
    private List<Long> codigosItens;

    public ResolverMultiplosItensOs() {

    }

    @NotNull
    public static ResolverMultiplosItensOs createDummy() {
        final ResolverMultiplosItensOs resolverItens = new ResolverMultiplosItensOs();
        resolverItens.setCpfColaboradorResolucao(12345678987L);
        resolverItens.setPlacaVeiculo("AAA1234");
        resolverItens.setDuracaoResolucaoItens(Duration.ofMinutes(10));
        resolverItens.setKmColetadoVeiculo(234000);
        resolverItens.setFeedbackResolucao("Tudo resolvido!");
        resolverItens.setCodUnidadeOrdemServico(5L);
        final List<Long> codItens = new ArrayList<>();
        codItens.add(1L);
        codItens.add(2L);
        codItens.add(3L);
        resolverItens.setCodigosItens(codItens);
        return resolverItens;
    }

    public Long getCodUnidadeOrdemServico() {
        return codUnidadeOrdemServico;
    }

    public void setCodUnidadeOrdemServico(final Long codUnidadeOrdemServico) {
        this.codUnidadeOrdemServico = codUnidadeOrdemServico;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(final String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public Long getCpfColaboradorResolucao() {
        return cpfColaboradorResolucao;
    }

    public void setCpfColaboradorResolucao(final Long cpfColaboradorResolucao) {
        this.cpfColaboradorResolucao = cpfColaboradorResolucao;
    }

    public Duration getDuracaoResolucaoItens() {
        return duracaoResolucaoItens;
    }

    public void setDuracaoResolucaoItens(final Duration duracaoResolucaoItens) {
        this.duracaoResolucaoItens = duracaoResolucaoItens;
    }

    public long getKmColetadoVeiculo() {
        return kmColetadoVeiculo;
    }

    public void setKmColetadoVeiculo(final long kmColetadoVeiculo) {
        this.kmColetadoVeiculo = kmColetadoVeiculo;
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