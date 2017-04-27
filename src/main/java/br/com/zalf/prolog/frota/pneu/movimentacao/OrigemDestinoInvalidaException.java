package br.com.zalf.prolog.frota.pneu.movimentacao;

import br.com.zalf.prolog.frota.pneu.movimentacao.destino.Destino;
import br.com.zalf.prolog.frota.pneu.movimentacao.origem.Origem;
import com.sun.istack.internal.NotNull;

/**
 * Created by Zart on 03/03/17.
 */
public final class OrigemDestinoInvalidaException extends Exception {

    public OrigemDestinoInvalidaException(@NotNull Origem origem, @NotNull Destino destino) {
        super(String.format("Você não pode mover um pneu do(a) %s para o(a) %s", origem, destino));
    }
}
