package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    @NotNull
    private final Long cpfColaboradoResolucao;

    /**
     * Código do Item que foi resolvido.
     */
    @NotNull
    private final Long codItemResolvido;

    /**
     * Descrição inserida pelo {@link Colaborador} no momento de resolução do Item.
     */
    @Nullable
    private final String feedbackResolucao;

    /**
     * Placa do {@link Veiculo} a qual o Item resolvido pertence.
     */
    @NotNull
    private final String placaVeiculo;

    /**
     * Quilometragem do {@link Veiculo} no momento de resolução do Item.
     */
    private final long kmColetadoVeiculo;

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
     * Código da Ordem de Serviço a qual o Item pertence.
     */
    @NotNull
    private final Long codOrdemServico;

    /**
     * O código da {@link Unidade} da qual o item da Ordem de Serviço pertence.
     */
    @NotNull
    private final Long codUnidadeOrdemServico;

    public ResolverItemOrdemServico(@NotNull final Long cpfColaboradoResolucao,
                                    @NotNull final Long codItemResolvido,
                                    @Nullable final String feedbackResolucao,
                                    @NotNull final String placaVeiculo,
                                    final long kmColetadoVeiculo,
                                    @NotNull final LocalDateTime dataHoraInicioResolucao,
                                    @NotNull final LocalDateTime dataHoraFimResolucao,
                                    @NotNull final Long codOrdemServico,
                                    @NotNull final Long codUnidadeOrdemServico) {
        this.cpfColaboradoResolucao = cpfColaboradoResolucao;
        this.codItemResolvido = codItemResolvido;
        this.feedbackResolucao = feedbackResolucao;
        this.placaVeiculo = placaVeiculo;
        this.kmColetadoVeiculo = kmColetadoVeiculo;
        this.dataHoraInicioResolucao = dataHoraInicioResolucao;
        this.dataHoraFimResolucao = dataHoraFimResolucao;
        this.codOrdemServico = codOrdemServico;
        this.codUnidadeOrdemServico = codUnidadeOrdemServico;
    }

    @NotNull
    public static ResolverItemOrdemServico createDummy() {
        return new ResolverItemOrdemServico(
                12345678987L,
                1L,
                "Resolvido!",
                "AAA1234",
                1234L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                10L,
                5L);
    }

    @NotNull
    public Long getCpfColaboradoResolucao() {
        return cpfColaboradoResolucao;
    }

    @NotNull
    public Long getCodItemResolvido() {
        return codItemResolvido;
    }

    @Nullable
    public String getFeedbackResolucao() {
        return feedbackResolucao;
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public long getKmColetadoVeiculo() {
        return kmColetadoVeiculo;
    }

    @NotNull
    public LocalDateTime getDataHoraInicioResolucao() {
        return dataHoraInicioResolucao;
    }

    @NotNull
    public LocalDateTime getDataHoraFimResolucao() {
        return dataHoraFimResolucao;
    }

    @NotNull
    public Long getCodOrdemServico() {
        return codOrdemServico;
    }

    @NotNull
    public Long getCodUnidadeOrdemServico() {
        return codUnidadeOrdemServico;
    }

    public long getDuracaoResolucaoMillis() {
        return ChronoUnit.MILLIS.between(dataHoraInicioResolucao, dataHoraFimResolucao);
    }
}