package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.api.error.ApiGenericException;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 04/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ItemResolvidoIntegracaoTransport {
    @NotNull
    private final Long codUnidadeOrdemServico;
    @NotNull
    private final Long codOrdemServico;
    @NotNull
    private final Long codItemResolvido;
    @NotNull
    private final String cpfColaboradorResolucao;
    /**
     * Placa do {@link Veiculo} a qual o item resolvido pertence.
     */
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final Long kmColetadoVeiculo;
    @NotNull
    private final Long duracaoResolucaoItemEmMilissegundos;
    @NotNull
    private final String feedbackResolucao;
    @NotNull
    private final LocalDateTime dataHoraResolvidoProLog;
    @NotNull
    private final LocalDateTime dataHoraInicioResolucao;
    @NotNull
    private final LocalDateTime dataHoraFimResolucao;

    public ItemResolvidoIntegracaoTransport(
            @NotNull final Long codUnidadeOrdemServico,
            @NotNull final Long codOrdemServico,
            @NotNull final Long codItemResolvido,
            @NotNull final String cpfColaboradorResolucao,
            @NotNull final String placaVeiculo,
            @NotNull final Long kmColetadoVeiculo,
            @NotNull final Long duracaoResolucaoItemEmMilissegundos,
            @NotNull final String feedbackResolucao,
            @NotNull final LocalDateTime dataHoraResolvidoProLog,
            @NotNull final LocalDateTime dataHoraInicioResolucao,
            @NotNull final LocalDateTime dataHoraFimResolucao) {
        this.codUnidadeOrdemServico = codUnidadeOrdemServico;
        this.codOrdemServico = codOrdemServico;
        this.codItemResolvido = codItemResolvido;
        this.cpfColaboradorResolucao = cpfColaboradorResolucao;
        this.placaVeiculo = placaVeiculo;
        this.kmColetadoVeiculo = kmColetadoVeiculo;
        this.duracaoResolucaoItemEmMilissegundos = duracaoResolucaoItemEmMilissegundos;
        this.feedbackResolucao = feedbackResolucao;
        this.dataHoraResolvidoProLog = dataHoraResolvidoProLog;
        this.dataHoraInicioResolucao = dataHoraInicioResolucao;
        this.dataHoraFimResolucao = dataHoraFimResolucao;
    }

    @NotNull
    public static ItemResolvidoIntegracaoTransport getDummy() {
        return new ItemResolvidoIntegracaoTransport(
                5L,
                94L,
                106851L,
                "03383283194",
                "PRO0001",
                90051L,
                900000L,
                "Item foi consertado.",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @NotNull(value = "O código da unidade não pode estar vazio", exception = ApiGenericException.class)
    public Long getCodUnidadeOrdemServico() {
        return codUnidadeOrdemServico;
    }

    @NotNull(value = "O código da Ordem de Serviço não pode estar vazio", exception = ApiGenericException.class)
    public Long getCodOrdemServico() {
        return codOrdemServico;
    }

    @NotNull(value = "O código do Item Resolvido não pode estar vazio", exception = ApiGenericException.class)
    public Long getCodItemResolvido() {
        return codItemResolvido;
    }

    @NotNull(value = "O CPF do colaborador não pode estar vazio", exception = ApiGenericException.class)
    public String getCpfColaboradorResolucao() {
        return cpfColaboradorResolucao;
    }

    @NotNull(value = "A placa do veículo não pode estar vazia", exception = ApiGenericException.class)
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @NotNull(value = "O KM do veículo não pode estar vazio", exception = ApiGenericException.class)
    public Long getKmColetadoVeiculo() {
        return kmColetadoVeiculo;
    }

    @NotNull(value = "A duração do conserto não pode estar vazia", exception = ApiGenericException.class)
    public Long getDuracaoResolucaoItemEmMilissegundos() {
        return duracaoResolucaoItemEmMilissegundos;
    }

    @NotNull(value = "O feedback do conserto não pode estar vazio", exception = ApiGenericException.class)
    public String getFeedbackResolucao() {
        return feedbackResolucao;
    }

    @NotNull(value = "A data e hora do conserto não pode estar vazio", exception = ApiGenericException.class)
    public LocalDateTime getDataHoraResolvidoProLog() {
        return dataHoraResolvidoProLog;
    }

    @NotNull(value = "A data e hora do início do conserto não pode estar vazio", exception = ApiGenericException.class)
    public LocalDateTime getDataHoraInicioResolucao() {
        return dataHoraInicioResolucao;
    }

    @NotNull(value = "A data e hora do fim do conserto não pode estar vazio", exception = ApiGenericException.class)
    public LocalDateTime getDataHoraFimResolucao() {
        return dataHoraFimResolucao;
    }
}
