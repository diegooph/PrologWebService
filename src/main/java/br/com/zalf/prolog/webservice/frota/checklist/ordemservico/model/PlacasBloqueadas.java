package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 26/10/2020
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public final class PlacasBloqueadas {
    @NotNull
    private final String nomeUnidade;
    @NotNull
    private final String placaBloqueada;
    @NotNull
    private final String dataHoraAberturaOs;
    private final int qtdItensCriticos;
}