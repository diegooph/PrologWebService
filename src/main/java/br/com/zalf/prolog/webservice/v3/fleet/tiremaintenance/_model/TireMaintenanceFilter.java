package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model;

import lombok.Getter;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

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
    TireMaintenanceStatus maintenanceStatus;
    @Nullable
    Long vehicleId;
    @Nullable
    Long tireId;
    int limit;
    int offset;

    @NotNull
    public Optional<TireMaintenanceStatus> getMaintenanceStatus() {
        return Optional.ofNullable(maintenanceStatus);
    }
}
