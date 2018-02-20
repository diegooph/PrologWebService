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
    private List<BarGroup> barGroups;

    public BarData(@NotNull List<BarGroup> barGroups) {
        this.barGroups = barGroups;
    }

    @NotNull
    public List<BarGroup> getBarEntries() {
        return barGroups;
    }

    public void setBarEntries(@NotNull List<BarGroup> barGroups) {
        this.barGroups = barGroups;
    }

    @Override
    public String toString() {
        return "BarData{" +
                "barGroups=" + barGroups +
                '}';
    }
}