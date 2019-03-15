package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
     * A data e hora em que a resolução do item foi iniciada pelo colaborador.
     */
    private LocalDateTime dataHoraInicioResolucao;

    /**
     * A data e hora em que a resolução do item foi finalizada pelo colaborador.
     */
    private LocalDateTime dataHoraFimResolucao;

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
        resolverItens.setDataHoraInicioResolucao(LocalDateTime.now());
        resolverItens.setDataHoraFimResolucao(LocalDateTime.now().plusDays(1));
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

    public long getDuracaoResolucaoMillis() {
        return ChronoUnit.MILLIS.between(dataHoraInicioResolucao, dataHoraFimResolucao);
    }
}