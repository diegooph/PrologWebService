package br.com.zalf.prolog.webservice.dashboard.components.charts.bar;

import br.com.zalf.prolog.webservice.dashboard.Color;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 20/02/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class BarGroup {

    @NotNull
    private String legenda;
    @NotNull
    private List<BarEntry> barEntries;
    @NotNull
    private Color corGrupo;

    public BarGroup(@NotNull String legenda, @NotNull List<BarEntry> barEntries, @NotNull Color corGrupo) {
        this.legenda = legenda;
        this.barEntries = barEntries;
        this.corGrupo = corGrupo;
    }

    @NotNull
    public String getLegenda() {
        return legenda;
    }

    public void setLegenda(@NotNull String legenda) {
        this.legenda = legenda;
    }

    @NotNull
    public List<BarEntry> getBarEntries() {
        return barEntries;
    }

    public void setBarEntries(@NotNull List<BarEntry> barEntries) {
        this.barEntries = barEntries;
    }

    @NotNull
    public Color getCorGrupo() {
        return corGrupo;
    }

    public void setCorGrupo(@NotNull Color corGrupo) {
        this.corGrupo = corGrupo;
    }

    @Override
    public String toString() {
        return "BarGroup{" +
                "legenda='" + legenda + '\'' +
                ", barEntries=" + barEntries +
                ", corGrupo=" + corGrupo +
                '}';
    }
}