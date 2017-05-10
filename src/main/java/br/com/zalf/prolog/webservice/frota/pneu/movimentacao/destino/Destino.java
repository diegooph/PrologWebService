package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.destino;

/**
 * Created by Zart on 23/02/17.
 */
public abstract class Destino {

    private final String tipo;

    protected Destino(String tipo) {
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