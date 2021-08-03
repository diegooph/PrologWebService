package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@AllArgsConstructor
@Getter
public enum TireMaintenanceStatus {
    OPEN(false),
    RESOLVED(true);

    @NotNull
    private final Boolean status;
}
