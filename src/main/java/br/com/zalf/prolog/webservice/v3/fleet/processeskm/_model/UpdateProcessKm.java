package br.com.zalf.prolog.webservice.v3.fleet.processeskm._model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Value
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class UpdateProcessKm {
    @NotNull
    Long companyId;
    @NotNull
    Long vehicleId;
    @NotNull
    Long processId;
    @NotNull
    VeiculoTipoProcesso processType;
    @Nullable
    Long userIdUpdate;
    @NotNull
    Long newKm;
}
