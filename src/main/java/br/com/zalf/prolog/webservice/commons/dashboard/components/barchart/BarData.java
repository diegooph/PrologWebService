package br.com.zalf.prolog.webservice.commons.dashboard.components.barchart;

import br.com.zalf.prolog.webservice.commons.dashboard.base.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class BarData extends Data {

    @NotNull
    private List<BarGroup> barGroups;

    public BarData(@NotNull List<BarGroup> barGroups) {
        this.barGroups = barGroups;
    }

    @NotNull
    public List<BarGroup> getBarGroups() {
        return barGroups;
    }

    public void setBarGroups(@NotNull List<BarGroup> barGroups) {
        this.barGroups = barGroups;
    }

    @Override
    public String toString() {
        return "BarData{" +
                "barGroups=" + barGroups +
                '}';
    }
}
