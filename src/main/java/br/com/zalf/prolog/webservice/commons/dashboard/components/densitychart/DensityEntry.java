package br.com.zalf.prolog.webservice.commons.dashboard.components.densitychart;

import br.com.zalf.prolog.webservice.commons.dashboard.base.Entry;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class DensityEntry extends Entry {

    private double x;
    private double y;

    public static DensityEntry create(double x, double y) {
        return new DensityEntry(x, y);
    }

    private DensityEntry(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "DensityEntry{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
