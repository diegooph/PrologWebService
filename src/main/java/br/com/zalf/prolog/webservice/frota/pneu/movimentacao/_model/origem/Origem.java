package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;

/**
 * Created by Zart on 23/02/17.
 */
public abstract class Origem {

    private final OrigemDestinoEnum tipo;

    protected Origem(OrigemDestinoEnum tipo) {
        this.tipo = tipo;
    }

    public OrigemDestinoEnum getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return tipo.asString();
    }
}