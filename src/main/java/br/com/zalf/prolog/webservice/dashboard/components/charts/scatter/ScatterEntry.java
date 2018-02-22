package br.com.zalf.prolog.webservice.dashboard.components.charts.scatter;

import br.com.zalf.prolog.webservice.dashboard.base.Entry;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ScatterEntry extends Entry {
    private double x;
    @NotNull
    private String representacaoX;
    private double y;
    @NotNull
    private String representacaoY;

    public static ScatterEntry create(final double x,
                                      @NotNull final String representacaoX,
                                      final double y,
                                      @NotNull final String representacaoY) {
        return new ScatterEntry(x, representacaoX, y, representacaoY);
    }

    private ScatterEntry(final double x,
                         @NotNull final String representacaoX,
                         final double y,
                         @NotNull final String representacaoY) {
        this.x = x;
        this.representacaoX = representacaoX;
        this.y = y;
        this.representacaoY = representacaoY;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @NotNull
    public String getRepresentacaoX() {
        return representacaoX;
    }

    public void setRepresentacaoX(@NotNull String representacaoX) {
        this.representacaoX = representacaoX;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @NotNull
    public String getRepresentacaoY() {
        return representacaoY;
    }

    public void setRepresentacaoY(@NotNull String representacaoY) {
        this.representacaoY = representacaoY;
    }

    @Override
    public String toString() {
        return "ScatterEntry{" +
                "x=" + x +
                ", representacaoX='" + representacaoX + '\'' +
                ", y=" + y +
                ", representacaoY='" + representacaoY + '\'' +
                '}';
    }
}