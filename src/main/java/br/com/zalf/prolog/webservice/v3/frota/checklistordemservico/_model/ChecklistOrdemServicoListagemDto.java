package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel(description = "Objeto com as informações de uma unidade.")
@Data
public class ChecklistOrdemServicoListagemDto {
    @ApiModelProperty(
            value = "Código da ordem de serviço.",
            example = "23")
    private final Long codOrdemServico;
    @ApiModelProperty(
            value = "Código único da unidade.",
            example = "215")
    private final Long codUnidadeOrdemServico;
    @ApiModelProperty(
            value = "Código do checklist que abriu essa ordem de serviço.",
            example = "10")
    private final Long codChecklistAbertura;
    private final Long codColaboradorAbertura;
    @NotNull
    private final String cpfColaboradorAbertura;
    @NotNull
    private final String nomeColaboradorAbertura;
    private final Long codVeiculo;
    @NotNull
    private final String placaVeiculo;
    @Nullable
    private final String identificadorFrota;
    @ApiModelProperty(
            value = "O status da ordem de serviço, aberta ou fechada.",
            example = "A")
    @NotNull
    private final StatusOrdemServico statusOrdemServico;
    @ApiModelProperty(
            value = "A data em que a O.S foi totalmente fechada, em UTC.",
            example = "2019-08-18T10:47:00")
    @Nullable
    private final LocalDateTime dataHoraFechamentoUtc;
    @ApiModelProperty(
            value = "A data em que a O.S foi totalmente fechada, com timezone aplicado.",
            example = "2019-08-18T10:47:00")
    @Nullable
    private final LocalDateTime dataHoraFechamentoTimeZoneAplicado;
    @ApiModelProperty(value = "Os itens da ordem de serviço.")
    @Nullable
    private final List<ChecklistOrdemServicoItemDto> itensOrdemServico;
}
