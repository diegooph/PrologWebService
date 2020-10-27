package br.com.zalf.prolog.webservice.frota.checklist.model;

import lombok.Data;

import java.util.List;

/**
 * Created on 26/10/2020
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public final class PlacasBloqueadasResponse {
    private final Integer qtdPlacasBloqueadas;
    private final List<PlacasBloqueadas> placasBloqueadas;
}