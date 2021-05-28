package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChecklistOrdemServicoListagemDto {
    @ApiModelProperty(value = "Código interno da ordem de serviço. Código único de identificação da ordem de serviço " +
            "a nível de sistema.",
                      required = true,
                      example = "732164")
    @NotNull
    private final Long codOrdemServicoProlog;
    @ApiModelProperty(value = "Código da ordem de serviço. Esse código é sequencial, gerado com base em cada unidade " +
            "da empresa, isso quer dizer que esse código poderá se repetir para cada unidade.",
                      required = true,
                      example = "12345")
    @NotNull
    private final Long codOrdemServico;
    @ApiModelProperty(value = "Código da unidade da ordem de serviço", required = true, example = "215")
    @NotNull
    private final Long codUnidadeOrdemServico;
    @ApiModelProperty(value = "Código do checklist que abriu a ordem de serviço", required = true, example = "12345")
    @NotNull
    private final Long codChecklistAbertura;
    @ApiModelProperty(value = "Código do colaborador que realizou o checklist. Como a ordem de serviço é aberta com " +
            "base no checklist, o colaborador que realiza o checklist é também o que abre a ordem de serviço.",
                      required = true,
                      example = "272")
    @NotNull
    private final Long codColaboradorAbertura;
    @ApiModelProperty(value = "Cpf do colaborador que realizou o checklist. Esse campo não possui nenhuma formatação.",
                      required = true,
                      example = "3383283194")
    @NotNull
    private final String cpfColaboradorAbertura;
    @ApiModelProperty(value = "Nome do colaborador que realizou o checklist", required = true, example = "Jean")
    @NotNull
    private final String nomeColaboradorAbertura;
    @ApiModelProperty(value = "Código do veículo que foi realizado o checklist. Como a ordem de serviço é aberta com " +
            "base no checklist, o veículo do checklist é também o veículo da ordem de serviço.",
                      required = true,
                      example = "12345")
    @NotNull
    private final Long codVeiculo;
    @ApiModelProperty(value = "Placa do veículo. Esse campo não possui nenhuma formatação.",
                      required = true,
                      example = "PRO1102")
    @NotNull
    private final String placaVeiculo;
    @ApiModelProperty(value = "Identificador de frota do veículo. Esse campo não possui nenhuma formatação.",
                      example = "FROTA01")
    @Nullable
    private final String identificadorFrota;
    @ApiModelProperty(value = "Data e hora de abertura da ordem de serviço. Valor expresso em UTC. É a mesma data e " +
            "hora em que o checklist foi realizado.",
                      required = true,
                      example = "2021-01-01T17:00:00")
    @NotNull
    private final LocalDateTime dataHoraAberturaUtc;
    @ApiModelProperty(value = "Data e hora de abertura da ordem de serviço. Valor expresso com Time Zone do cliente " +
            "aplicado. O Time Zone do cliente é configurado por Unidade. É a mesma data e shora em que o checklist " +
            "foi realizado.",
                      required = true,
                      example = "2021-01-01T14:00:00")
    @NotNull
    private final LocalDateTime dataHoraAberturaTimeZoneAplicado;
    @ApiModelProperty(value = "Status da ordem de serviço. Podendo ser ABERTA ou FECHADA.",
                      required = true,
                      example = "FECHADA")
    @NotNull
    private final StatusOrdemServico statusOrdemServico;
    @ApiModelProperty(value = "Data e hora de fechamento da ordem de serviço. Valor expresso em UTC. A data e hora de" +
            " fechamento condiz com a data e hora de fechamento do último item da ordem de serviço.",
                      example = "2021-01-01T17:00:00")
    @Nullable
    private final LocalDateTime dataHoraFechamentoUtc;
    @ApiModelProperty(value = "Data e hora de fechamento da ordem de serviço. Valor expresso com Time Zone do cliente" +
            " aplicado. O Time Zone do cliente é configurado por Unidade.",
                      example = "2021-01-01T14:00:00")
    @Nullable
    private final LocalDateTime dataHoraFechamentoTimeZoneAplicado;
    @ApiModelProperty(value = "Itens da ordem de serviço. Por padrão, essas lista é sempre retornada, porém, caso não" +
            " tenha necessidade dessa informação, pode optar por não incluir no momento da requisição.")
    @Nullable
    private final List<ChecklistOrdemServicoItemDto> itensOrdemServico;
}
