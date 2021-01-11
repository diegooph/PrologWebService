package br.com.zalf.prolog.webservice.frota.pneu._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-12-07
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class PneuRetornoDescarte {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final Long codPneu;
    @NotNull
    private final Long codColaborador;
    @Nullable
    private final String motivoRetornoDescarte;
}
