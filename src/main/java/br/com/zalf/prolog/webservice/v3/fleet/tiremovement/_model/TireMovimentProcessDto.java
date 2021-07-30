package br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2021-04-26
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class TireMovimentProcessDto {
    @ApiModelProperty(value = "Código do processo de movimentação. Em um processo podem ser feitas várias " +
            "movimentações.",
                      required = true,
                      example = "12345")
    @NotNull
    private final Long codProcessoMovimentacao;
    @ApiModelProperty(value = "Código da unidade onde as movimentações foram realizadas",
                      required = true,
                      example = "215")
    @NotNull
    private final Long codUnidadeProcessoMovimentacao;
    @ApiModelProperty(value = "Data e hora que as movimentações foram realizadas. Valor expresso em UTC.",
                      required = true,
                      example = "2021-01-01T17:00:00")
    @NotNull
    private final LocalDateTime dataHoraRealizacaoUtc;
    @ApiModelProperty(value = "Data e hora que as movimentações foram realizadas. Valor expresso com Time Zone do " +
            "cliente aplicado. O Time Zone do cliente é configurado por Unidade.",
                      required = true,
                      example = "2021-01-01T14:00:00")
    @NotNull
    private final LocalDateTime dataHoraRealizacaoTimeZoneAplicado;
    @ApiModelProperty(value = "Código do colaborador que realizou as movimentações", required = true, example = "272")
    @NotNull
    private final Long codColaborador;
    @ApiModelProperty(value = "Cpf do colaborador que realizou as movimentações. Esse campo não possui nenhuma " +
            "formatação.",
                      required = true,
                      example = "3383283194")
    @NotNull
    private final String cpfColaborador;
    @ApiModelProperty(value = "Nome do colaborador que realizou as movimentações", required = true, example = "Jean")
    @NotNull
    private final String nomeColaborador;
    @ApiModelProperty(value = "Código do veículo que foi realizado as movimentações", example = "12345")
    @Nullable
    private final Long codVeiculo;
    @ApiModelProperty(value = "Placa do veículo. Esse campo não possui nenhuma formatação.",
                      example = "PRO1102")
    @Nullable
    private final String placaVeiculo;
    @ApiModelProperty(value = "Identificador de frota do veículo. Esse campo não possui nenhuma formatação.",
                      example = "FROTA01")
    @Nullable
    private final String identificadorFrota;
    @ApiModelProperty(value = "Km do veículo que no momento das movimentações.", example = "111111")
    @Nullable
    private final Long kmColetadoVeiculo;
    @ApiModelProperty(value = "Código do diagrama do veículo. Esse código identifica a estrutura de chassi do veículo.",
                      example = "1")
    @Nullable
    private final Long codDiagramaVeiculo;
    @ApiModelProperty(value = "Observação inserida pelo colaborador.",
                      example = "Movimentações de rodízio")
    @Nullable
    private final String observacaoProcessoMovimentacao;
    @ApiModelProperty(value = "Movimentações realizadas no processo. Essa lista conterá uma entrada para cada " +
            "movimentação realizada no mesmo process. Um processo de movimentação sempre terá, ao menos, uma " +
            "movimentação podendo ter várias.")
    @NotNull
    private final List<TireMovementDto> movimentacoesRealizadas;
}
