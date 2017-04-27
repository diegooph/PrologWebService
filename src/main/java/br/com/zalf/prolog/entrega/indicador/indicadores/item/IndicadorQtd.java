package br.com.zalf.prolog.entrega.indicador.indicadores.item;

/**
 * Created by jean on 31/08/16.
 */
public abstract class IndicadorQtd extends IndicadorItem {

    private double ok;
    private double nok;
    private double meta;
    private double resultado;

    public IndicadorQtd(){super();}

    public double getOk() {
        return ok;
    }

    public IndicadorQtd setOk(double ok) {
        this.ok = ok;
        return this;
    }

    public double getNok() {
        return nok;
    }

    public IndicadorQtd setNok(double nok) {
        this.nok = nok;
        return this;
    }

    public double getMeta() {
        return meta;
    }

    public IndicadorQtd setMeta(double meta) {
        this.meta = meta;
        return this;
    }

    public Double getResultado() {
        return resultado;
    }

    public IndicadorQtd setResultado(Double resultado) {
        this.resultado = resultado;
        return this;
    }

    @Override
    public String toString() {
        return "IndicadorQtd{" +
                super.toString()+
                ", ok=" + ok +
                ", nok=" + nok +
                ", meta=" + meta +
                ", resultado=" + resultado +
                '}';
    }
}
