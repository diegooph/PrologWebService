package br.com.zalf.prolog.entrega.indicador.indicadores.item;

import java.sql.Time;
import java.time.Duration;
import java.util.Date;

/**
 * Created by jean on 01/09/16.
 */
public class TempoRota extends IndicadorTempo {

    public static final String TEMPO_ROTA = "TEMPO_ROTA";

    private Time hrEntrada;
    private Time hrSaida;

    public TempoRota() {
        super();
    }

    @Override
    public TempoRota setBateuMeta(boolean bateuMeta) {
        super.setBateuMeta(bateuMeta);
        return this;
    }

    @Override
    public TempoRota setMeta(Duration meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public TempoRota setData(Date data) {
        super.setData(data);
        return this;
    }

    @Override
    public TempoRota setMapa(int mapa) {
        super.setMapa(mapa);
        return this;
    }

    public Time getHrEntrada() {
        return hrEntrada;
    }

    public TempoRota setHrEntrada(Time hrEntrada){
        this.hrEntrada = hrEntrada;
        return this;
    }

    public Time getHrSaida() {
        return hrSaida;
    }

    public TempoRota setHrSaida(Time hrSaida) {
        this.hrSaida = hrSaida;
        return this;
    }

    public String getTipo(){
        return TEMPO_ROTA;
    }

    public void calculaResultado(){
        super.setResultado(Duration.ofSeconds(hrEntrada.toLocalTime().toSecondOfDay()).minus(Duration.ofSeconds(hrSaida.toLocalTime().toSecondOfDay())));
        setBateuMeta(getResultado().getSeconds() <= getMeta().getSeconds());
    }

    @Override
    public String toString() {
        return "TempoRota{" +
                super.toString() +
                "hrEntrada=" + hrEntrada +
                ", hrSaida=" + hrSaida + ", Resultado=" +
                getResultado() +
                '}';
    }

}
