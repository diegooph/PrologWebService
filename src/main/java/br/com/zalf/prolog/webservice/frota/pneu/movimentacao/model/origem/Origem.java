package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem;

/**
 * Created by Zart on 23/02/17.
 */
public abstract class Origem {

    private final String tipo;

    protected Origem(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return tipo;
    }
}