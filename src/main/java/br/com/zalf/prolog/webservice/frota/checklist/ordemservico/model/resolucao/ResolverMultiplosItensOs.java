package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
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
    @NotNull
    private final Long cpfColaboradorResolucao;

    /**
     * Placa do {@link Veiculo} a qual os Itens resolvidos pertencem.
     */
    @NotNull
    private final String placaVeiculo;

    /**
     * A data e hora em que a resolução do item foi iniciada pelo colaborador.
     */
    @NotNull
    private final LocalDateTime dataHoraInicioResolucao;

    /**
     * A data e hora em que a resolução do item foi finalizada pelo colaborador.
     */
    @NotNull
    private final LocalDateTime dataHoraFimResolucao;

    /**
     * Quilometragem do {@link Veiculo} no momento de resolução dos Itens.
     */
    private final long kmColetadoVeiculo;

    /**
     * Descrição inserida pelo {@link Colaborador} no momento de resolução dos Itens.
     */
    @Nullable
    private final String feedbackResolucao;

    /**
     * O código da {@link Unidade} da qual os itens da Ordem de Serviço pertencem.
     */
    @NotNull
    private final Long codUnidadeOrdemServico;

    /**
     * Códigos dos itens que foram resolvidos.
     */
    @NotNull
    private final List<Long> codigosItens;

    public ResolverMultiplosItensOs(@NotNull final Long cpfColaboradorResolucao,
                                    @NotNull final String placaVeiculo,
                                    @NotNull final LocalDateTime dataHoraInicioResolucao,
                                    @NotNull final LocalDateTime dataHoraFimResolucao,
                                    final long kmColetadoVeiculo,
                                    @Nullable final String feedbackResolucao,
                                    @NotNull final Long codUnidadeOrdemServico,
                                    @NotNull final List<Long> codigosItens) {
        this.cpfColaboradorResolucao = cpfColaboradorResolucao;
        this.placaVeiculo = placaVeiculo;
        this.dataHoraInicioResolucao = dataHoraInicioResolucao;
        this.dataHoraFimResolucao = dataHoraFimResolucao;
        this.kmColetadoVeiculo = kmColetadoVeiculo;
        this.feedbackResolucao = feedbackResolucao;
        this.codUnidadeOrdemServico = codUnidadeOrdemServico;
        this.codigosItens = codigosItens;
    }

    @NotNull
    public static ResolverMultiplosItensOs createDummy() {
        final List<Long> codItens = new ArrayList<>();
        codItens.add(1L);
        codItens.add(2L);
        codItens.add(3L);
        return new ResolverMultiplosItensOs(
                12345678987L,
                "AAA1234",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                234000,
                "Tudo resolvido!",
                5L,
                codItens);
    }

    @NotNull
    public static ResolverMultiplosItensOs createFrom(@NotNull final ResolverItemOrdemServico item) {
        return new ResolverMultiplosItensOs(
                item.getCpfColaboradoResolucao(),
                item.getPlacaVeiculo(),
                item.getDataHoraInicioResolucao(),
                item.getDataHoraFimResolucao(),
                item.getKmColetadoVeiculo(),
                item.getFeedbackResolucao(),
                item.getCodUnidadeOrdemServico(),
                Collections.singletonList(item.getCodItemResolvido()));
    }

    @NotNull
    public Long getCpfColaboradorResolucao() {
        return cpfColaboradorResolucao;
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @NotNull
    public LocalDateTime getDataHoraInicioResolucao() {
        return dataHoraInicioResolucao;
    }

    @NotNull
    public LocalDateTime getDataHoraFimResolucao() {
        return dataHoraFimResolucao;
    }

    public long getKmColetadoVeiculo() {
        return kmColetadoVeiculo;
    }

    @Nullable
    public String getFeedbackResolucao() {
        return feedbackResolucao;
    }

    @NotNull
    public Long getCodUnidadeOrdemServico() {
        return codUnidadeOrdemServico;
    }

    @NotNull
    public List<Long> getCodigosItens() {
        return codigosItens;
    }

    public long getDuracaoResolucaoMillis() {
        return ChronoUnit.MILLIS.between(dataHoraInicioResolucao, dataHoraFimResolucao);
    }
}