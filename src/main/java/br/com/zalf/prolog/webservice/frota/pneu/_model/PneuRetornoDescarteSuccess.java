package br.com.zalf.prolog.webservice.frota.pneu._model;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-12-07
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
@Builder(toBuilder = true)
public final class PneuRetornoDescarteSuccess {
    @NotNull
    private final Long codPneuRetornado;
    @NotNull
    private final Long codMovimentacaoGerada;
    @Nullable
    private final String msg;
}
