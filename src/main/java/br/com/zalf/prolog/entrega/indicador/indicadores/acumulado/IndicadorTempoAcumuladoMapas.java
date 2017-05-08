package br.com.zalf.prolog.entrega.indicador.indicadores.acumulado;

/**
 * Created by jean on 01/09/16.
 */
public abstract  class IndicadorTempoAcumuladoMapas extends IndicadorAcumulado {

    private int mapasOk;
    private int mapasNok;
    private double meta;
    private double resultado;

    public IndicadorTempoAcumuladoMapas() {
        super();
    }

    public int getMapasOk() {
        return mapasOk;
    }

    public IndicadorTempoAcumuladoMapas setMapasOk(int mapasOk) {
        this.mapasOk = mapasOk;
        return this;
    }

    public int getMapasNok() {
        return mapasNok;
    }

    public IndicadorTempoAcumuladoMapas setMapasNok(int mapasNok) {
        this.mapasNok = mapasNok;
        return this;
    }

    public double getMeta() {
        return meta;
    }

    public IndicadorTempoAcumuladoMapas setMeta(double meta) {
        this.meta = meta;
        return this;
    }

    public double getResultado() {
        return resultado;
    }

    public IndicadorTempoAcumuladoMapas setResultado(double resultado) {
        this.resultado = resultado;
        return this;
    }

    public abstract String getTipo();

    public int getTotal(){
        return getMapasOk() + getMapasNok();
    }
}
