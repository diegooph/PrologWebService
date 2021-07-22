package br.com.zalf.prolog.webservice.v3.fleet.inspection._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Created on 2021-05-27
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Data
public final class InspectionMeasurePk implements Serializable {
    @NotNull
    private Long inspectionId;
    @NotNull
    private Long tireId;
}
