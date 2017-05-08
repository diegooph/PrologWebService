package br.com.zalf.prolog.entrega.indicador.indicadores.acumulado;

/**
 * Created by jean on 03/09/16.
 */
public abstract class IndicadorQtdAcumulado extends IndicadorAcumulado {

    private int totalOk;
    private int totalNok;
    private double meta;
    private double resultado;

    public IndicadorQtdAcumulado() {
        super();
    }

    public int getTotalOk() {
        return totalOk;
    }

    public IndicadorQtdAcumulado setTotalOk(int totalOk) {
        this.totalOk = totalOk;
        return this;
    }

    public int getTotalNok() {
        return totalNok;
    }

    public IndicadorQtdAcumulado setTotalNok(int totalNok) {
        this.totalNok = totalNok;
        return this;
    }

    public double getMeta() {
        return meta;
    }

    public IndicadorQtdAcumulado setMeta(double meta) {
        this.meta = meta;
        return this;
    }

    public double getResultado() {
        return resultado;
    }

    public IndicadorQtdAcumulado setResultado(double resultado) {
        this.resultado = resultado;
        return this;
    }

    public int getTotal(){
        return totalNok + totalOk;
    }

    @Override
    public String toString() {
        return "IndicadorQtdAcumulado{" +
                super.toString() +
                " totalOk=" + totalOk +
                ", totalNok=" + totalNok +
                ", meta=" + meta +
                ", resultado=" + resultado +
                '}';
    }
}
