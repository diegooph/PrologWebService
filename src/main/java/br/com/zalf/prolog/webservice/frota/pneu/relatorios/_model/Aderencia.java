package br.com.zalf.prolog.webservice.frota.pneu.relatorios._model;

/**
 * Created by jean on 11/07/16.
 */
public class Aderencia {

    private int dia;
    private int realizadas;
    private double meta;

    public Aderencia() {
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getRealizadas() {
        return realizadas;
    }

    public void setRealizadas(int realizadas) {
        this.realizadas = realizadas;
    }

    public double getMeta() {
        return meta;
    }

    public void setMeta(double meta) {
        this.meta = meta;
    }

    public double getResultado() {
        return this.realizadas/this.meta;
    }


    @Override
    public String toString() {
        return "Aderencia{" +
                "dia=" + dia +
                ", realizadas=" + realizadas +
                ", meta=" + meta +
                ", resultado=" + getResultado() +
                '}';
    }
}
