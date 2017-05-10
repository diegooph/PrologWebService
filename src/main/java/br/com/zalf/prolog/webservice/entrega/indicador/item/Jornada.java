package br.com.zalf.prolog.webservice.entrega.indicador.item;

import java.time.Duration;
import java.util.Date;

/**
 * Created by jean on 01/09/16.
 */
public class Jornada extends IndicadorTempo{

    public static final String JORNADA = "JORNADA";

    private Duration tempoLargada;
    private Duration tempoRota;
    private Duration tempoInterno;

    public Jornada() {
        super();
    }

    @Override
    public Jornada setBateuMeta(boolean bateuMeta) {
        super.setBateuMeta(bateuMeta);
        return this;
    }

    @Override
    public Jornada setMeta(Duration meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public Jornada setData(Date data) {
        super.setData(data);
        return this;
    }

    public String getTipo(){
        return JORNADA;
    }

    public void calculaResultado(){
        super.setResultado(tempoLargada.plus(tempoRota).plus(tempoInterno));
        setBateuMeta(getResultado().getSeconds() <= getMeta().getSeconds());
    }

    @Override
    public Jornada setMapa(int mapa) {
        super.setMapa(mapa);
        return this;
    }

    public Duration getTempoLargada() {
        return tempoLargada;
    }

    public Jornada setTempoLargada(Duration tempoLargada) {
        this.tempoLargada = tempoLargada;
        return this;
    }

    public Duration getTempoRota() {
        return tempoRota;
    }

    public Jornada setTempoRota(Duration tempoRota) {
        this.tempoRota = tempoRota;
        return this;
    }

    public Duration getTempoInterno() {
        return tempoInterno;
    }

    public Jornada setTempoInterno(Duration tempoInterno) {
        this.tempoInterno = tempoInterno;
        return this;
    }

    @Override
    public String toString() {
        return "Jornada{" +
                super.toString() +
                "tempoLargada=" + tempoLargada +
                ", tempoRota=" + tempoRota +
                ", tempoInterno=" + tempoInterno +
                '}';
    }
}
