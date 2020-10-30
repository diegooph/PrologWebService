package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 26/10/2020
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public final class PlacasBloqueadasResponse {
    @NotNull
    private Integer qtdPlacasBloqueadas;
    @NotNull
    private List<PlacasBloqueadas> placasBloqueadas;
}