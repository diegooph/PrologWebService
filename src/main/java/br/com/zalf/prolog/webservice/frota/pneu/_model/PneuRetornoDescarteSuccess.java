package br.com.zalf.prolog.webservice.frota.pneu._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-12-07
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class PneuRetornoDescarteSuccess {
    @NotNull
    private final Long codPneuRetornado;
    @NotNull
    private final Long codMovimentacaoGerada;
}
