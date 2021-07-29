package br.com.zalf.prolog.webservice.v3.fleet.vehicle._model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2021-06-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
@Value
public class AttachedVehiclesDto {
    @ApiModelProperty(value = "Código do processo de acoplamento.", required = true, example = "1")
    @NotNull
    Long attachedProcessId;
    @ApiModelProperty(value = "Informações de cada veículo presente no acoplamento.")
    @NotNull
    List<AttachedVehicleDto> attachedVehicles;
}
