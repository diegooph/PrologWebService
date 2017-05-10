package br.com.zalf.prolog.webservice.entrega.indicador.item;

import java.time.Duration;

/**
 * Created by jean on 31/08/16.
 */
public abstract class IndicadorTempo extends IndicadorItem {

    private Duration meta;
    private Duration resultado;

    public IndicadorTempo(){
        super();
    }

    public Duration getMeta() {
        return meta;
    }

    public IndicadorTempo setMeta(Duration meta) {
        this.meta = meta;
        return this;
    }

    public Duration getResultado(){
        return resultado;
    }

    public IndicadorTempo setResultado(Duration resultado) {
        this.resultado = resultado;
        return this;
    }

    public abstract String getTipo();

    @Override
    public String toString() {
        return "IndicadorTempo{" +
                super.toString() +
                ", meta=" + meta +
                ", resultado=" + resultado +
                '}';
    }
}
