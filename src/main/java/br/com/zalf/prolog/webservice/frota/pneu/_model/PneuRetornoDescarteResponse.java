package br.com.zalf.prolog.webservice.frota.pneu._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-01-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class PneuRetornoDescarteResponse {
    @NotNull
    private final Long codPneuRetornado;
    @NotNull
    private final Long codMovimentacaoGerada;
    @NotNull
    private final String msg;
}
