package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.Destino;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.Origem;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Zart on 03/03/17.
 */
public final class OrigemDestinoInvalidaException extends Exception {

    public OrigemDestinoInvalidaException(@NotNull Origem origem, @NotNull Destino destino) {
        super(String.format("Você não pode mover um pneu do(a) %s para o(a) %s", origem, destino));
    }
}
