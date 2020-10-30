package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 26/10/2020
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public final class PlacasBloqueadasResponse {
    @NotNull
    private final Integer qtdPlacasBloqueadas;
    @NotNull
    private final List<PlacasBloqueadas> placasBloqueadas;
}