package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model;

import com.google.gson.annotations.SerializedName;

import java.time.Duration;
import java.time.LocalDateTime;

public class ConsertoMultiplosItensOs {

    private Long codColaboradorConserto;
    /**
     * Data em que o item foi apontado pela primeira vez
     */
    private LocalDateTime dataApontamento;
    /**
     * data e hora em que o item foi marcado como consertado
     */
    private LocalDateTime dataHoraConserto;
    /**
     * Km do veículo no momento em que o item foi fechado
     */
    private long kmVeiculoFechamento;
    private ItemOrdemServico.Status status;
    /**
     * Tempo que o item levou para ser consertado
     */
    @SerializedName("tempoRealizacaoConsertoEmSegundos")
    private Duration tempoRealizacaoConserto;
    /**
     * Prazo em horas para conserto do item
     */
    @SerializedName("tempoLimiteResolucaoEmSegundos")
    private Duration tempoLimiteResolucao;
    /**
     * Tempo restante para consertar o item, baseado na sua prioridade
     */
    @SerializedName("tempoRestanteEmSegundos")
    private Duration tempoRestante;
    /**
     * Observação do serviço realizado
     */
    private String feedbackResolucao;
    /**
     * Quantidade de apontamentos que um item tem
     */
    private int qtdApontamentos;

    /**
     * Código sequencial do item.
     */
    private Long codigo;
}
