package br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Data
public final class ChecklistWorkOrderItemDto {
    @ApiModelProperty(value = "Código do item da ordem de serviço", required = true, example = "12345")
    @NotNull
    private final Long codItemOrdemServico;
    @ApiModelProperty(value = "Código da pergunta que abriu o item",
                      required = true,
                      example = "12345")
    @NotNull
    private final Long codPerguntaPrimeiroApontamento;
    @ApiModelProperty(value = "Código de contexto da pergunta que abriu o item",
                      required = true,
                      example = "12345")
    @NotNull
    private final Long codContextoPergunta;
    @ApiModelProperty(value = "Código da alternativa que abriu o item",
                      required = true,
                      example = "12345")
    @NotNull
    private final Long codAlternativaPrimeiroApontamento;
    @ApiModelProperty(value = "Código de contexto da alternativa que abriu o item",
                      required = true,
                      example = "12345")
    @NotNull
    private final Long codContextoAlternativa;
    @ApiModelProperty(value = "Código auxiliar da alternativa que abriu o item. Esta propriedade só é enviada caso " +
            "for configurada na alternativa.",
                      example = "Serviço=FREIO")
    @Nullable
    private final String codAuxiliarAlternativaPrimeiroApontamento;
    @ApiModelProperty(value = "Status do item. Podendo ser RESOLVIDO ou PENDENTE.",
                      required = true,
                      example = "RESOLVIDO")
    @NotNull
    private final StatusItemOrdemServico statusItemOrdemServico;
    @ApiModelProperty(value = "Quantidade de vezes que esse item foi apontado antes de ser corrigido. Sempre que um " +
            "checklist é realizado na placa, e o item não está RESOLVIDO, é incrementado a quantidade de apontamentos.",
                      required = true,
                      example = "12345")
    private final int quantidadeApontamentos;
    @ApiModelProperty(value = "Código do colaborador que resolveu o item.", example = "272")
    @Nullable
    private final Long codColaboradorResolucao;
    @ApiModelProperty(value = "Cpf do colaborador que resolveu o item. Esse campo não possui nenhuma formatação.",
                      example = "3383283194")
    @Nullable
    private final Long cpfColaboradorResolucao;
    @ApiModelProperty(value = "Nome do colaborador que resolveu o item.", example = "Jean")
    @Nullable
    private final String nomeColaboradorResolucao;
    @ApiModelProperty(value = "Km do veículo que no momento de resolução do item.", required = true, example = "111111")
    @Nullable
    private final Long kmVeiculoMomentoResolucao;
    @ApiModelProperty(value = "Código do lote resolvido. Caso o item tenha sido fechado através do processo de " +
            "resolução em lote.",
                      example = "11")
    @Nullable
    private final Long codAgrupamentoResolucaoEmLote;
    @ApiModelProperty(value = "Data e hora que os dados de resolução do item foram enviados para o Prolog. Valor " +
            "expresso em UTC.",
                      example = "2021-01-01T17:00:00")
    @Nullable
    private final LocalDateTime dataHoraConsertoUtc;
    @ApiModelProperty(value = "Data e hora que os dados de resolução do item foram enviados para o Prolog. Valor " +
            "expresso com Time Zone do cliente aplicado. O Time Zone do cliente é configurado por Unidade.",
                      example = "2021-01-01T14:00:00")
    @Nullable
    private final LocalDateTime dataHoraConsertoTimeZoneAplicado;
    @ApiModelProperty(value = "Data e hora que o colaborador iniciou a resolução do item. Valor expresso em UTC.",
                      example = "2021-01-01T17:00:00")
    @Nullable
    private final LocalDateTime dataHoraInicioResolucaoUtc;
    @ApiModelProperty(value = "Data e hora que o colaborador iniciou a resolução do item. Valor expresso com Time " +
            "Zone do cliente aplicado. O Time Zone do cliente é configurado por Unidade.",
                      example = "2021-01-01T14:00:00")
    @Nullable
    private final LocalDateTime dataHoraInicioResolucaoTimeZoneAplicado;
    @ApiModelProperty(value = "Data e hora que o colaborador finalizou a resolução do item. Valor expresso em UTC.",
                      example = "2021-01-01T17:00:00")
    @Nullable
    private final LocalDateTime dataHoraFimResolucaoUtc;
    @ApiModelProperty(value = "Data e hora que o colaborador finalizou a resolução do item. Valor expresso com Time " +
            "Zone do cliente aplicado. O Time Zone do cliente é configurado por Unidade.",
                      example = "2021-01-01T14:00:00")
    @Nullable
    private final LocalDateTime dataHoraFimResolucaoTimeZoneAplicado;
    @ApiModelProperty(value = "Diferença de tempo em o início e a finalização do item.",
                      example = "36000")
    @Nullable
    private final Long tempoResolucaoEmMilisegundos;
    @ApiModelProperty(value = "Observação inserida pelo colaborador ao resolver o item.",
                      example = "Item resolvido")
    @Nullable
    private final String observacaoResolucao;
}
