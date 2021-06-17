package br.com.zalf.prolog.webservice.v3.frota.veiculo._model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-06-04
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
@Value
public class VeiculoListagemDto {
    @ApiModelProperty(value = "Código do veículo.", required = true, example = "12345")
    @NotNull
    Long codVeiculo;
    @ApiModelProperty(value = "Placa do veículo acoplado. Esse campo não possui nenhuma formatação.",
                      required = true,
                      example = "PRO1102")
    @NotNull
    String placaVeiculo;
    @ApiModelProperty(value = "Identificador de frota do veículo. Esse campo não possui nenhuma formatação.",
                      example = "FROTA01")
    @Nullable
    String identificadorFrota;
    @ApiModelProperty(value = "Indica se o veículo possui motor. Caso possua: TRUE, se não possuir: FALSE",
                      required = true, example = "true")
    boolean veiculoMotorizado;
    @ApiModelProperty(value = "Indica se a carreta possui hunbodômetro. Caso possua: TRUE, se não possuir: FALSE",
                      required = true, example = "true")
    boolean possuiHubodometro;
    @ApiModelProperty(value = "Código da marca do veículo", required = true, example = "1")
    @NotNull
    Long codMarca;
    @ApiModelProperty(value = "Nome da marca", required = true, example = "Marca A1")
    @NotNull
    String nomeMarca;
    @ApiModelProperty(value = "Código do modelo do veículo", required = true, example = "1")
    @NotNull
    Long codModelo;
    @ApiModelProperty(value = "Nome do modelo do veículo", required = true, example = "Modelo A1")
    @NotNull
    String nomeModelo;
    @ApiModelProperty(value = "Código do diagrama do veículo", required = true, example = "1")
    @NotNull
    Long codDiagrama;
    @ApiModelProperty(value = "Quantidade de eixos dianteiros", required = true, example = "1")
    @NotNull
    Long qtdEixosDianteiros;
    @ApiModelProperty(value = "Quantidade de eixos traseiros", required = true, example = "1")
    @NotNull
    Long qtdEixosTraseiro;
    @ApiModelProperty(value = "Código do tipo de veículo.", required = true, example = "1")
    @NotNull
    Long codTipo;
    @ApiModelProperty(value = "Nome do tipo de veículo.", required = true, example = "Tipo A1")
    @NotNull
    String nomeTipo;
    @ApiModelProperty(value = "Código da unidade a qual o veículo se encontra.", required = true, example = "1")
    @NotNull
    Long codUnidade;
    @ApiModelProperty(value = "Nome da unidade a qual o veículo se encontra.", required = true, example = "Unidade A1")
    @NotNull
    String nomeUnidade;
    @ApiModelProperty(value = "Código da região a qual a unidade se encontra.", required = true, example = "1")
    @NotNull
    Long codRegionalAlocado;
    @ApiModelProperty(value = "Nome da região a qual a unidade se encontra.", required = true, example = "Região A1")
    @NotNull
    String nomeRegionalAlocado;
    @ApiModelProperty(value = "Km atual do veículo.", required = true, example = "1")
    @NotNull
    Long kmAtual;
    @ApiModelProperty(value = "Status do veículo. No prolog o veículo pode estar ATIVO ou INATIVO," +
            "consideramos TRUE para ativo e FALSE para inativo.", required = true, example = "true")
    @NotNull
    boolean statusAtivo;
    @ApiModelProperty(value = "Quantidade de pneus aplicados.")
    int totalPneusAplicados;
    @ApiModelProperty(value = "Indica se o veículo possui acoplamento. Caso possua: TRUE, se não possuir: FALSE",
                      required = true,
                      example = "true")
    boolean acoplado;
    @ApiModelProperty(value = "Caso hajam veículos acoplados, lista quais são.")
    @Nullable
    VeiculosAcopladosListagemDto veiculosAcoplados;
}
