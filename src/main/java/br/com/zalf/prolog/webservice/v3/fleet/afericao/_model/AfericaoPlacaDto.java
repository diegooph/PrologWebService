package br.com.zalf.prolog.webservice.v3.fleet.afericao._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2021-02-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class AfericaoPlacaDto {
    @ApiModelProperty(value = "Código da aferição", required = true, example = "12345")
    @NotNull
    Long codAfericao;
    @ApiModelProperty(value = "Código da unidade onde a aferição foi realizada", required = true, example = "215")
    @NotNull
    Long codUnidadeAfericao;
    @ApiModelProperty(value = "Código do colaborador que realizou a aferição", required = true, example = "272")
    @NotNull
    Long codColaborador;
    @ApiModelProperty(value = "Cpf do colaborador que realizou a aferição. Esse campo não possui nenhuma formatação.",
                      required = true,
                      example = "3383283194")
    @NotNull
    String cpfColaborador;
    @ApiModelProperty(value = "Nome do colaborador que realizou a aferição", required = true, example = "Jean")
    @NotNull
    String nomeColaborador;
    @ApiModelProperty(value = "Código do veículo que foi aferido", required = true, example = "12345")
    @NotNull
    Long codVeiculo;
    @ApiModelProperty(value = "Placa do veículo que foi aferido. Esse campo não possui nenhuma formatação.",
                      required = true,
                      example = "PRO1102")
    @NotNull
    String placaVeiculo;
    @ApiModelProperty(value = "Identificador de frota do veículo. Esse campo não possui nenhuma formatação.",
                      example = "FROTA01")
    @Nullable
    String identificadorFrota;
    @ApiModelProperty(value = "Km do veículo que no momento da aferição.", required = true, example = "111111")
    long kmVeiculoAfericao;
    @ApiModelProperty(value = "Data e hora que o veículo foi aferido. Valor expresso em UTC.",
                      required = true,
                      example = "2021-01-01T17:00:00")
    @NotNull
    LocalDateTime dataHoraRealizacaoUtc;
    @ApiModelProperty(value = "Data e hora que o veículo foi aferido. Valor expresso com Time Zone do cliente " +
            "aplicado. O Time Zone do cliente é configurado por Unidade.",
                      required = true,
                      example = "2021-01-01T14:00:00")
    @NotNull
    LocalDateTime dataHoraRealizacaoTimeZoneAplicado;
    @ApiModelProperty(value = "Tipo de medição realizada no veículo. Valores podem ser: SULCO, PRESSAO ou " +
            "SULCO_PRESSAO. Em medições de SULCO, a pressão dos pneus não é informada. Em medições de PRESSAO, os " +
            "sulcos dos pneus não são informados. Em medições de SULCO_PRESSAO, todas as informações são fornecidas.",
                      required = true,
                      example = "SULCO_PRESSAO")
    @NotNull
    TipoMedicaoColetadaAfericao tipoMedicaoColetada;
    @ApiModelProperty(value = "Tipo de processo de coleta realizado no veículo. Esse valor será sempre PLACA. Indica " +
            "que a coleta foi realizada em todos os pneus do veículo.",
                      required = true,
                      example = "PLACA")
    @NotNull
    TipoProcessoColetaAfericao tipoProcessoColeta;
    @ApiModelProperty(value = "Tempo que o colaborador demorou para realizar a aferição no veículo.",
                      required = true,
                      example = "36000")
    long tempoRealizacaoEmMilisegundos;
    @ApiModelProperty(value = "Forma com que o veículo foi aferido. Valores podem ser: EQUIPAMENTO ou MANUAL. Em " +
            "coletas com EQUIPAMENTO, as medidas são enviadas automaticamente pelo Aferidor. Nas coletas MANUAL, as " +
            "medidas são inseridas manualmente pelo colaborador.",
                      required = true,
                      example = "EQUIPAMENTO")
    @NotNull
    FormaColetaDadosAfericaoEnum formaColetaDados;
    @ApiModelProperty(value = "Medidas coletadas no processo de aferição do veículo. Essa lista conterá uma entrada " +
            "para cada pneu presente no veículo no momento da aferição. Por padrão, essas lista é sempre retornada, " +
            "porém, caso não tenha necessidade dessa informação, pode optar por não incluir no momento da requisição.")
    @Nullable
    List<MedidaDto> medidasColetadas;
}