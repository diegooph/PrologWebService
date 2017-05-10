package br.com.zalf.prolog.webservice.entrega.indicador.item;

import java.sql.Time;
import java.time.Duration;
import java.util.Date;

/**
 * Created by jean on 01/09/16.
 */
public class TempoLargada extends IndicadorTempo{

    public static final String TEMPO_LARGADA = "TEMPO_LARGADA";

    private Time hrMatinal;
    private Time hrSaida;

    public TempoLargada() {
        super();
    }

    @Override
    public TempoLargada setBateuMeta(boolean bateuMeta) {
        super.setBateuMeta(bateuMeta);
        return this;
    }

    @Override
    public TempoLargada setMeta(Duration meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public TempoLargada setData(Date data) {
        super.setData(data);
        return this;
    }

    @Override
    public TempoLargada setMapa(int mapa) {
        super.setMapa(mapa);
        return this;
    }

    public Time getHrMatinal() {
        return hrMatinal;
    }

    public TempoLargada setHrMatinal(Time hrMatinal) {
        this.hrMatinal = hrMatinal;
        return this;
    }

    public Time getHrSaida() {
        return hrSaida;
    }

    public TempoLargada setHrSaida(Time hrSaida) {
        this.hrSaida = hrSaida;
        return this;
    }

    public String getTipo(){
        return TEMPO_LARGADA;
    }

    public void calculaResultado(){
        super.setResultado(Duration.ofSeconds(hrSaida.toLocalTime().toSecondOfDay()).minus(Duration.ofSeconds(hrMatinal.toLocalTime().toSecondOfDay())));
        setBateuMeta(getResultado().getSeconds() <= getMeta().getSeconds());
    }

    @Override
    public String toString() {
        return "TempoLargada{" +
                super.toString() +
                "hrMatinal=" + hrMatinal +
                ", hrSaida=" + hrSaida +
                getResultado() +
                '}';
    }

}
