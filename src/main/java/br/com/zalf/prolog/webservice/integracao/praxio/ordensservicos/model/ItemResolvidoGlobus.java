package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Classe que contém os atributos de um Item Resolvido no Sistema Globus.
 * <p>
 * Created on 02/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ItemResolvidoGlobus {
    /**
     * Código da Unidade onde o item foi fechado.
     * <p>
     * Este código deve ser {@link OrdemServicoAbertaGlobus#codUnidadeItemOs}.
     */
    @NotNull
    private final Long codUnidadeItemOs;
    /**
     * Código da Ordem de Serviço que teve um item fechado no Globus.
     * <p>
     * Este código deve ser o mesmo que {@link OrdemServicoAbertaGlobus#codOsGlobus}.
     */
    @NotNull
    private final Long codOsGlobus;
    /**
     * Código do Item que foi resolvido no Globus.
     * <p>
     * Este código deve ser o mesmo que {@link ItemOSAbertaGlobus#codItemGlobus}.
     */
    @NotNull
    private final Long codItemResolvidoGlobus;
    /**
     * CPF do colaborador resolveu o Item no Sistema Globus.
     */
    @NotNull
    private final String cpfColaboradorResolucao;
    /**
     * Placa do veículo que teve o Item resolvido.
     */
    @NotNull
    private final String placaVeiculoItemOs;
    /**
     * Quilometragem do veículo quando o Item foi resolvido.
     */
    @NotNull
    private final Long kmColetadoResolucao;
    /**
     * Duração da resolução do item pelo colaborador.
     * <p>
     * Este valor deverá ser a diferença entre {@link #dataHoraFimResolucaoItemOsUtc} e
     * {@link #dataHoraInicioResolucaoItemOsUtc} expressa em milisegundos.
     */
    @NotNull
    private final Long duracaoResolucaoItemOsMillis;
    /**
     * Observação inserida pelo colaborador no momento de resolver o item.
     */
    @NotNull
    private final String feedbackResolucaoItemOs;
    /**
     * Data e hora em que o item foi resolvido no Sistema Globus.
     */
    @NotNull
    private final LocalDateTime dataHoraResolucaoItemOsUtc;
    /**
     * Data e hora em que o colaborador começou a resolução do item no Sistema Globus.
     */
    @NotNull
    private final LocalDateTime dataHoraInicioResolucaoItemOsUtc;
    /**
     * Data e hora em que o colaborador finalizou a resolução do item no Sistema Globus.
     */
    @NotNull
    private final LocalDateTime dataHoraFimResolucaoItemOsUtc;

    public ItemResolvidoGlobus(@NotNull final Long codUnidadeItemOs,
                               @NotNull final Long codOsGlobus,
                               @NotNull final Long codItemResolvidoGlobus,
                               @NotNull final String cpfColaboradorResolucao,
                               @NotNull final String placaVeiculoItemOs,
                               @NotNull final Long kmColetadoResolucao,
                               @NotNull final Long duracaoResolucaoItemOsMillis,
                               @NotNull final String feedbackResolucaoItemOs,
                               @NotNull final LocalDateTime dataHoraResolucaoItemOsUtc,
                               @NotNull final LocalDateTime dataHoraInicioResolucaoItemOsUtc,
                               @NotNull final LocalDateTime dataHoraFimResolucaoItemOsUtc) {
        this.codUnidadeItemOs = codUnidadeItemOs;
        this.codOsGlobus = codOsGlobus;
        this.codItemResolvidoGlobus = codItemResolvidoGlobus;
        this.cpfColaboradorResolucao = cpfColaboradorResolucao;
        this.placaVeiculoItemOs = placaVeiculoItemOs;
        this.kmColetadoResolucao = kmColetadoResolucao;
        this.duracaoResolucaoItemOsMillis = duracaoResolucaoItemOsMillis;
        this.feedbackResolucaoItemOs = feedbackResolucaoItemOs;
        this.dataHoraResolucaoItemOsUtc = dataHoraResolucaoItemOsUtc;
        this.dataHoraInicioResolucaoItemOsUtc = dataHoraInicioResolucaoItemOsUtc;
        this.dataHoraFimResolucaoItemOsUtc = dataHoraFimResolucaoItemOsUtc;
    }

    @NotNull
    public static ItemResolvidoGlobus getDummy() {
        return new ItemResolvidoGlobus(
                5L,
                1L,
                100L,
                "03383283194",
                "PRO0001",
                54939L,
                Duration.ofHours(2L).getSeconds(),
                "Item consertado através da integração com o Globus",
                Now.getLocalDateTimeUtc(),
                Now.getLocalDateTimeUtc().minus(Duration.ofHours(1)),
                Now.getLocalDateTimeUtc().minus(Duration.ofMinutes(15)));
    }

    @NotNull
    public Long getCodUnidadeItemOs() {
        return codUnidadeItemOs;
    }

    @NotNull
    public Long getCodOsGlobus() {
        return codOsGlobus;
    }

    @NotNull
    public Long getCodItemResolvidoGlobus() {
        return codItemResolvidoGlobus;
    }

    @NotNull
    public String getCpfColaboradorResolucao() {
        return cpfColaboradorResolucao;
    }

    @NotNull
    public String getPlacaVeiculoItemOs() {
        return placaVeiculoItemOs;
    }

    @NotNull
    public Long getKmColetadoResolucao() {
        return kmColetadoResolucao;
    }

    @NotNull
    public Long getDuracaoResolucaoItemOsMillis() {
        return duracaoResolucaoItemOsMillis;
    }

    @NotNull
    public String getFeedbackResolucaoItemOs() {
        return feedbackResolucaoItemOs;
    }

    @NotNull
    public LocalDateTime getDataHoraResolucaoItemOsUtc() {
        return dataHoraResolucaoItemOsUtc;
    }

    @NotNull
    public LocalDateTime getDataHoraInicioResolucaoItemOsUtc() {
        return dataHoraInicioResolucaoItemOsUtc;
    }

    @NotNull
    public LocalDateTime getDataHoraFimResolucaoItemOsUtc() {
        return dataHoraFimResolucaoItemOsUtc;
    }
}
