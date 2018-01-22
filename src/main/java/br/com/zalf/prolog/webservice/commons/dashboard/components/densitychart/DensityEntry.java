package br.com.zalf.prolog.webservice.commons.dashboard.components.densitychart;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class DensityEntry {

    private double x;
    private double y;

    public DensityEntry(double x, double y) {
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
