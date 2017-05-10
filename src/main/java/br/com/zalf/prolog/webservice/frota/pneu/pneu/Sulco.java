package br.com.zalf.prolog.webservice.frota.pneu.pneu;

/**
 * Created by jean on 04/04/16.
 */
public class Sulco {

    private double interno;
    private double central;
    private double externo;

    public Sulco() {
    }

    public Sulco(double interno, double central, double externo) {
        this.interno = interno;
        this.central = central;
        this.externo = externo;
    }

    public double getInterno() {
        return interno;
    }

    public void setInterno(double interno) {
        this.interno = interno;
    }

    public double getCentral() {
        return central;
    }

    public void setCentral(double central) {
        this.central = central;
    }

    public double getExterno() {
        return externo;
    }

    public void setExterno(double externo) {
        this.externo = externo;
    }

    @Override
    public String toString() {
        return "Sulco{" +
                "interno=" + interno +
                ", central=" + central +
                ", externo=" + externo +
                '}';
    }
}
