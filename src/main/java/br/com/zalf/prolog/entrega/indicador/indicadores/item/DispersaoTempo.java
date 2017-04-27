package br.com.zalf.prolog.entrega.indicador.indicadores.item;

import java.time.Duration;
import java.util.Date;

/**
 * Created by jean on 01/09/16.
 */
public class DispersaoTempo extends IndicadorItem {

    public static final String DISPERSAO_TEMPO = "DISPERSAO_TEMPO";

    private Duration previsto;
    private Duration realizado;
    private Duration disperso;
    private double resultado;
    private double meta;

    public DispersaoTempo() {
        super();
    }

    public String getTipo(){
        return DISPERSAO_TEMPO;
    }

    public Duration getPrevisto() {
        return previsto;
    }

    public DispersaoTempo setPrevisto(Duration previsto) {
        this.previsto = previsto;
        return this;

    }

    public Duration getRealizado() {
        return realizado;
    }

    public DispersaoTempo setRealizado(Duration realizado) {
        this.realizado = realizado;
        return this;

    }

    public Duration getDisperso() {
        return disperso;
    }

    public DispersaoTempo setDisperso(Duration disperso) {
        this.disperso = disperso;
        return this;
    }

    public double getResultado() {
        return resultado;
    }

    public DispersaoTempo setResultado(double resultado) {
        this.resultado = resultado;
        return this;
    }

    public double getMeta() {
        return meta;
    }

    public DispersaoTempo setMeta(double meta) {
        this.meta = meta;
        return this;
    }

    public void calculaResultado(){
        disperso = realizado.minus(previsto);
        if(!disperso.isZero() && !previsto.isZero()){
            resultado = (float) realizado.minus(previsto).getSeconds() / previsto.getSeconds();
        }else {
            resultado = 0;
        }
        setBateuMeta(resultado <= meta);
    }

    @Override
    public DispersaoTempo setMapa(int mapa) {
        super.setMapa(mapa);
        return this;
    }

    @Override
    public DispersaoTempo setBateuMeta(boolean bateuMeta) {
        super.setBateuMeta(bateuMeta);
        return this;
    }

    @Override
    public DispersaoTempo setData(Date data) {
        super.setData(data);
        return this;
    }

    @Override
    public String toString() {
        return "DispersaoTempo{" +
                super.toString() +
                "previsto=" + previsto +
                ", realizado=" + realizado +
                ", disperso=" + disperso +
                ", resultado=" + resultado +
                ", meta=" + meta +
                '}';
    }
}
