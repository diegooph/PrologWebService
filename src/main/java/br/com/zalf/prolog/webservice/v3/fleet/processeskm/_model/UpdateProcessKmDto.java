package br.com.zalf.prolog.webservice.v3.fleet.processeskm._model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Value
public class UpdateProcessKmDto {
    @NotNull(message = "The company ID cannot be null.")
    Long companyId;
    @NotNull(message = "The vehicle ID cannot be null.")
    Long vehicleId;
    @NotNull(message = "The process ID cannot be null.")
    Long processId;
    @NotNull(message = "The process type cannot be null.")
    VeiculoTipoProcesso processType;
    @NotNull(message = "The new km cannot be null.")
    @PositiveOrZero(message = "The new km cannot be a negative value.")
    Long newKm;
}
