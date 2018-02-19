package br.com.zalf.prolog.webservice.dashboard.components.charts.bar;

import br.com.zalf.prolog.webservice.dashboard.base.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class BarData extends Data {
    @NotNull
    private List<BarEntry> barEntries;

    public BarData(@NotNull List<BarEntry> barEntries) {
        this.barEntries = barEntries;
    }

    @NotNull
    public List<BarEntry> getBarEntries() {
        return barEntries;
    }

    public void setBarEntries(@NotNull List<BarEntry> barEntries) {
        this.barEntries = barEntries;
    }

    @Override
    public String toString() {
        return "BarData{" +
                "barEntries=" + barEntries +
                '}';
    }
}