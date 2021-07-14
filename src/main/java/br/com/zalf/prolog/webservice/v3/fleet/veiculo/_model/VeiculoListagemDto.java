package br.com.zalf.prolog.webservice.v3.fleet.veiculo._model;

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
    @ApiModelProperty(value = "Placa do veículo.", required = true, example = "PRO1102")
    @NotNull
    String placaVeiculo;
    @ApiModelProperty(value = "Identificador de frota do veículo.", example = "FROTA01")
    @Nullable
    String identificadorFrota;
    @ApiModelProperty(value = "Indica se o veículo possui motor.", required = true, example = "true")
    boolean veiculoMotorizado;
    @ApiModelProperty(value = "Indica se a carreta possui hunbodômetro.", required = true, example = "true")
    boolean possuiHubodometro;
    @ApiModelProperty(value = "Código da marca do veículo.", required = true, example = "43")
    @NotNull
    Long codMarcaVeiculo;
    @ApiModelProperty(value = "Nome da marca do veículo.", required = true, example = "Volkswagen")
    @NotNull
    String nomeMarcaVeiculo;
    @ApiModelProperty(value = "Código do modelo do veículo.", required = true, example = "120")
    @NotNull
    Long codModeloVeiculo;
    @ApiModelProperty(value = "Nome do modelo do veículo.", required = true, example = "VW 2220")
    @NotNull
    String nomeModeloVeiculo;
    @ApiModelProperty(value = "Código do diagrama do veículo. Esse código identifica a estrutura de chassi do veículo.",
                      required = true,
                      example = "1")
    @NotNull
    Short codDiagramaVeiculo;
    @ApiModelProperty(value = "Quantidade de eixos dianteiros, presentes na estrutura do veículo.",
                      required = true,
                      example = "1")
    @NotNull
    Long qtdEixosDianteiros;
    @ApiModelProperty(value = "Quantidade de eixos traseiros, presentes na estrutura do veículo.",
                      required = true,
                      example = "2")
    @NotNull
    Long qtdEixosTraseiro;
    @ApiModelProperty(value = "Código do tipo de veículo.", required = true, example = "12345")
    @NotNull
    Long codTipoVeiculo;
    @ApiModelProperty(value = "Nome do tipo de veículo.", required = true, example = "TRUCK")
    @NotNull
    String nomeTipoVeiculo;
    @ApiModelProperty(value = "Código da unidade onde o veículo está alocado.", required = true, example = "215")
    @NotNull
    Long codUnidadeVeiculo;
    @ApiModelProperty(value = "Nome da unidade onde o pneu está alocado.",
                      required = true,
                      example = "Unidade de testes")
    @NotNull
    String nomeUnidadeVeiculo;
    @ApiModelProperty(value = "Código do grupo da unidade.", required = true, example = "1")
    @NotNull
    Long codGrupoVeiculo;
    @ApiModelProperty(value = "Nome do grupo da unidade.", required = true, example = "Sudeste")
    @NotNull
    String nomeGrupoVeiculo;
    @ApiModelProperty(value = "Km atual do veículo.", required = true, example = "111111")
    @NotNull
    Long kmAtualVeiculo;
    @ApiModelProperty(value = "Status do veículo.", required = true, example = "true")
    boolean statusAtivo;
    @ApiModelProperty(value = "Quantidade de pneus aplicados ao veículo.")
    int totalPneusAplicados;
    @ApiModelProperty(value = "Flag indicando se o veículo está acoplado.", required = true, example = "true")
    boolean veiculoAcoplado;
    @ApiModelProperty(value = "Posição em que o veículo está acoplado. Essa propriedade só será enviada caso o " +
            "veículo estiver acoplado.",
                      example = "1")
    @Nullable
    Short posicaoVeiculoAcoplado;
    @ApiModelProperty(value = "Informações do acoplamento em que o veículo se encontra. Essa propriedade só será " +
            "enviada caso o veículo estiver acoplado.")
    @Nullable
    VeiculosAcopladosListagemDto veiculosAcoplados;
}
