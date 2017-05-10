package br.com.zalf.prolog.webservice.entrega.indicador.acumulado;

import java.time.Duration;

/**
 * Created by jean on 08/09/16.
 */
public abstract class IndicadorTempoAcumuladoMedia extends IndicadorAcumulado {

    private Duration meta;
    private Duration resultado;

    public IndicadorTempoAcumuladoMedia() {
        super();
    }

    public void calculaResultado(){
        super.setBateuMeta(resultado.getSeconds() <= meta.getSeconds() );
    }

    public Duration getMeta() {
        return meta;
    }

    public IndicadorTempoAcumuladoMedia setMeta(Duration meta) {
        this.meta = meta;
        return this;
    }

    public Duration getResultado() {
        return resultado;
    }

    public IndicadorTempoAcumuladoMedia setResultado(Duration resultado) {
        this.resultado = resultado;
        return this;
    }

    public abstract String getTipo();

    @Override
    public String toString() {
        return "IndicadorTempoAcumuladoMedia{" +
                super.toString() +
                "meta=" + meta +
                ", resultado=" + resultado +
                '}';
    }
}
