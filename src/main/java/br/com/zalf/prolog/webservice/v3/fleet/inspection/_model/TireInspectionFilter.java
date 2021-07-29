package br.com.zalf.prolog.webservice.v3.fleet.inspection._model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class TireInspectionFilter {
    @NotNull
    List<Long> branchesId;
    @NotNull
    LocalDate initialDate;
    @NotNull
    LocalDate finalDate;
    int limit;
    int offset;
    boolean includeMeasures;
}