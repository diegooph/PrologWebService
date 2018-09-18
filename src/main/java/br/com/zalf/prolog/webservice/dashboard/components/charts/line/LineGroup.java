package br.com.zalf.prolog.webservice.dashboard.components.charts.line;

import br.com.zalf.prolog.webservice.dashboard.Color;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 18/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class LineGroup {
    @NotNull
    private final String legenda;
    @NotNull
    private final List<LineEntry> lineEntries;
    @NotNull
    private final Color lineColor;

    public LineGroup(@NotNull String legenda, @NotNull List<LineEntry> lineEntries, @NotNull Color lineColor) {
        this.legenda = legenda;
        this.lineEntries = lineEntries;
        this.lineColor = lineColor;
    }

    @NotNull
    public String getLegenda() {
        return legenda;
    }

    @NotNull
    public List<LineEntry> getLineEntries() {
        return lineEntries;
    }

    @NotNull
    public Color getLineColor() {
        return lineColor;
    }
}