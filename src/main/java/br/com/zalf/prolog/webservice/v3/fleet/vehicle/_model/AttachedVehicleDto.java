package br.com.zalf.prolog.webservice.v3.fleet.vehicle._model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-06-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
@Value
public class AttachedVehicleDto {
    @ApiModelProperty(value = "Código do veículo acoplado.", required = true, example = "1")
    @NotNull
    Long codVeiculoAcoplado;
    @ApiModelProperty(value = "Placa do veículo acoplado.", required = true, example = "PRO1111")
    @NotNull
    String placaVeiculoAcoplado;
    @ApiModelProperty(value = "Identificador de frota do veículo acoplado.", example = "FROTA01")
    @Nullable
    String identificadorFrotaAcoplado;
    @ApiModelProperty(value = "Flag que indica se o veículo possuí motor.", required = true, example = "true")
    boolean motorizado;
    @ApiModelProperty(value = "Posição em que o veículo está acoplado.", required = true, example = "1")
    Short posicaoAcoplado;
}
