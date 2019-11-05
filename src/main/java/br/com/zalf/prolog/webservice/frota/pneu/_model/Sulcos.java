package br.com.zalf.prolog.webservice.frota.pneu._model;

/**
 * Created by jean on 04/04/16.
 */
public class Sulcos {
    private Double interno;
    private Double centralInterno;
    private Double centralExterno;
    private Double externo;

    public Sulcos() {

    }

    public Double getInterno() {
        return interno;
    }

    public void setInterno(Double interno) {
        this.interno = interno;
    }

    public Double getCentralInterno() {
        return centralInterno;
    }

    public void setCentralInterno(Double centralInterno) {
        this.centralInterno = centralInterno;
    }

    public Double getCentralExterno() {
        return centralExterno;
    }

    public void setCentralExterno(Double centralExterno) {
        this.centralExterno = centralExterno;
    }

    public Double getExterno() {
        return externo;
    }

    public void setExterno(Double externo) {
        this.externo = externo;
    }

    public double getMenorSulco() {
        return Math.min(Math.min(Math.min(externo, centralExterno), centralInterno), interno);
    }

    @Override
    public String toString() {
        return "Sulcos{" +
                "interno=" + interno +
                ", centralInterno=" + centralInterno +
                ", centralExterno=" + centralExterno +
                ", externo=" + externo +
                '}';
    }
}