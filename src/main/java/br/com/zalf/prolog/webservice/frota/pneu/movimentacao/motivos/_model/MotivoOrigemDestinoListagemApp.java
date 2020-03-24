package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2020-03-23
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public @Data
class MotivoOrigemDestinoListagemApp {

    @Nullable
    private final List<MotivoVisualizacaoApp> motivos;

    @NotNull
    private final OrigemDestinoEnum origem;

    @NotNull
    private final OrigemDestinoEnum destino;

    @NotNull
    private final boolean obrigatorio;

}
