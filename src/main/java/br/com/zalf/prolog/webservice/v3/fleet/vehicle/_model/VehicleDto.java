package br.com.zalf.prolog.webservice.v3.fleet.vehicle._model;

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
public class VehicleDto {
    @ApiModelProperty(value = "Código do veículo.", required = true, example = "12345")
    @NotNull
    Long vehicleId;
    @ApiModelProperty(value = "Placa do veículo.", required = true, example = "PRO1102")
    @NotNull
    String vehiclePlate;
    @ApiModelProperty(value = "Identificador de frota do veículo.", example = "FROTA01")
    @Nullable
    String fleetId;
    @ApiModelProperty(value = "Indica se o veículo possui motor.", required = true, example = "true")
    boolean hasEngine;
    @ApiModelProperty(value = "Indica se a carreta possui hunbodômetro.", required = true, example = "true")
    boolean hasHubodometer;
    @ApiModelProperty(value = "Código da marca do veículo.", required = true, example = "43")
    @NotNull
    Long vehicleMakeId;
    @ApiModelProperty(value = "Nome da marca do veículo.", required = true, example = "Volkswagen")
    @NotNull
    String vehicleMakeName;
    @ApiModelProperty(value = "Código do modelo do veículo.", required = true, example = "120")
    @NotNull
    Long vehicleModelId;
    @ApiModelProperty(value = "Nome do modelo do veículo.", required = true, example = "VW 2220")
    @NotNull
    String vehicleModelName;
    @ApiModelProperty(value = "Código do diagrama do veículo. Esse código identifica a estrutura de chassi do veículo.",
                      required = true,
                      example = "1")
    @NotNull
    Short vehicleLayoutId;
    @ApiModelProperty(value = "Quantidade de eixos dianteiros, presentes na estrutura do veículo.",
                      required = true,
                      example = "1")
    @NotNull
    Long frontAxleQuantity;
    @ApiModelProperty(value = "Quantidade de eixos traseiros, presentes na estrutura do veículo.",
                      required = true,
                      example = "2")
    @NotNull
    Long rearAxleQuantity;
    @ApiModelProperty(value = "Código do tipo de veículo.", required = true, example = "12345")
    @NotNull
    Long vehicleTypeId;
    @ApiModelProperty(value = "Nome do tipo de veículo.", required = true, example = "TRUCK")
    @NotNull
    String vehicleTypeName;
    @ApiModelProperty(value = "Código da unidade onde o veículo está alocado.", required = true, example = "215")
    @NotNull
    Long vehicleBranchId;
    @ApiModelProperty(value = "Nome da unidade onde o pneu está alocado.",
                      required = true,
                      example = "Unidade de testes")
    @NotNull
    String vehicleBranchName;
    @ApiModelProperty(value = "Código do grupo da unidade.", required = true, example = "1")
    @NotNull
    Long vehicleGroupId;
    @ApiModelProperty(value = "Nome do grupo da unidade.", required = true, example = "Sudeste")
    @NotNull
    String vehicleGroupName;
    @ApiModelProperty(value = "Km atual do veículo.", required = true, example = "111111")
    @NotNull
    Long vehicleKm;
    @ApiModelProperty(value = "Status do veículo.", required = true, example = "true")
    boolean isActive;
    @ApiModelProperty(value = "Quantidade de pneus aplicados ao veículo.")
    int totalAppliedTires;
    @ApiModelProperty(value = "Flag indicando se o veículo está acoplado.", required = true, example = "true")
    boolean isAttached;
    @ApiModelProperty(value = "Posição em que o veículo está acoplado. Essa propriedade só será enviada caso o " +
            "veículo estiver acoplado.",
                      example = "1")
    @Nullable
    Short positionAttached;
    @ApiModelProperty(value = "Informações do acoplamento em que o veículo se encontra. Essa propriedade só será " +
            "enviada caso o veículo estiver acoplado.")
    @Nullable
    VeiculosAcopladosListagemDto attachedVehicles;
}
