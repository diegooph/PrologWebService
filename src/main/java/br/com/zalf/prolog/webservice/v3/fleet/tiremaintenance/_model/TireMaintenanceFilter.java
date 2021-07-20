package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model;

import lombok.Getter;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
@Getter
public class TireMaintenanceFilter {
    @NotNull
    List<Long> branchesId;
    @Nullable
    ServicoPneuStatus maintenanceStatus;
    @Nullable
    Long vehicleId;
    @Nullable
    Long tireId;
    int limit;
    int offset;
}
