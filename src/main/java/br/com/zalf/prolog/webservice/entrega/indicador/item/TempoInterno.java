package br.com.zalf.prolog.webservice.entrega.indicador.item;

import java.sql.Time;
import java.time.Duration;
import java.util.Date;

/**
 * Created by jean on 01/09/16.
 */
public class TempoInterno extends IndicadorTempo {

    public static final String TEMPO_INTERNO = "TEMPO_INTERNO";

    private Time hrEntrada;
    private Time hrFechamento;

    public TempoInterno() {
        super();
    }

    @Override
    public TempoInterno setBateuMeta(boolean bateuMeta) {
        super.setBateuMeta(bateuMeta);
        return this;
    }

    @Override
    public TempoInterno setMeta(Duration meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public TempoInterno setData(Date data) {
        super.setData(data);
        return this;
    }

    @Override
    public TempoInterno setMapa(int mapa) {
        super.setMapa(mapa);
        return this;
    }


    public Time getHrEntrada() {
        return hrEntrada;
    }

    public TempoInterno setHrEntrada(Time hrEntrada) {
        this.hrEntrada = hrEntrada;
        return this;
    }

    public Time getHrFechamento() {
        return hrFechamento;
    }

    public TempoInterno setHrFechamento(Time hrFechamento) {
        this.hrFechamento = hrFechamento;
        return this;
    }

    public String getTipo(){
        return TEMPO_INTERNO;
    }

    public void calculaResultado(){
        super.setResultado(Duration.ofSeconds(hrFechamento.toLocalTime().toSecondOfDay()).minus(Duration.ofSeconds(hrEntrada.toLocalTime().toSecondOfDay())));
        setBateuMeta(getResultado().getSeconds() <= getMeta().getSeconds());
    }

    @Override
    public String toString() {
        return "TempoInterno{" +
                super.toString() +
                "hrEntrada=" + hrEntrada +
                ", hrFechamento=" + hrFechamento +
                "Resultado=" + getResultado() +
                '}';
    }
}
