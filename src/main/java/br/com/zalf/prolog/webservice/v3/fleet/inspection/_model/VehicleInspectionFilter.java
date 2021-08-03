package br.com.zalf.prolog.webservice.v3.fleet.inspection._model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class VehicleInspectionFilter {
    @NotNull
    List<Long> branchesId;
    @NotNull
    LocalDate startDate;
    @NotNull
    LocalDate endDate;
    @Nullable
    Long vehicleId;
    @Nullable
    Long vehicleTypeId;
    boolean includeMeasures;
    int limit;
    int offset;
}
