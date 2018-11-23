package br.com.zalf.prolog.webservice.dashboard.components.charts.scatter;

import br.com.zalf.prolog.webservice.dashboard.base.Entry;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ScatterEntry extends Entry {
    private final double x;
    @NotNull
    private final String representacaoX;
    private final double y;
    @NotNull
    private final String representacaoY;

    /**
     * A ideia é que essa info seja exibida quando o usuário clicar em um ponto do gráfico.
     */
    @NotNull
    private final String infoEntry;

    @NotNull
    public static ScatterEntry create(final double x,
                                      @NotNull final String representacaoX,
                                      final double y,
                                      @NotNull final String representacaoY,
                                      @NotNull final String infoEntry) {
        return new ScatterEntry(x, representacaoX, y, representacaoY, infoEntry);
    }

    private ScatterEntry(final double x,
                         @NotNull final String representacaoX,
                         final double y,
                         @NotNull final String representacaoY,
                         @NotNull final String infoEntry) {
        this.x = x;
        this.representacaoX = representacaoX;
        this.y = y;
        this.representacaoY = representacaoY;
        this.infoEntry = infoEntry;
    }

    public double getX() {
        return x;
    }

    @NotNull
    public String getRepresentacaoX() {
        return representacaoX;
    }

    public double getY() {
        return y;
    }

    @NotNull
    public String getRepresentacaoY() {
        return representacaoY;
    }

    @NotNull
    public String getInfoEntry() {
        return infoEntry;
    }
}