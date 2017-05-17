package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino.Destino;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.Origem;
import com.sun.istack.internal.NotNull;

/**
 * Created by Zart on 03/03/17.
 */
public final class OrigemDestinoInvalidaException extends Exception {

    public OrigemDestinoInvalidaException(@NotNull Origem origem, @NotNull Destino destino) {
        super(String.format("Você não pode mover um pneu do(a) %s para o(a) %s", origem, destino));
    }
}
