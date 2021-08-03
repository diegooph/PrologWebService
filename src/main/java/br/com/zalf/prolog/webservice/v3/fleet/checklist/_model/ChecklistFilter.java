package br.com.zalf.prolog.webservice.v3.fleet.checklist._model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-04-09
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Value(staticConstructor = "of")
public class ChecklistFilter {
    @NotNull
    List<Long> branchesId;
    @NotNull
    LocalDate startDate;
    @NotNull
    LocalDate endDate;
    @Nullable
    Long userId;
    @Nullable
    Long vehicleId;
    @Nullable
    Long vehicleTypeId;
    boolean includeAnswers;
    int limit;
    int offset;
}
