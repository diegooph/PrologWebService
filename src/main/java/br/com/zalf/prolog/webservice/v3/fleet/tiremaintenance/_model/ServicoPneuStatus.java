package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@AllArgsConstructor
@Getter
public enum ServicoPneuStatus {
    ABERTO(false), FECHADO(true);

    private final Boolean asBoolean;
}
