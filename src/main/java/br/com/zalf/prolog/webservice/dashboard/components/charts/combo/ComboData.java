package br.com.zalf.prolog.webservice.dashboard.components.charts.combo;

import br.com.zalf.prolog.webservice.dashboard.base.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ComboData extends Data {

    @NotNull
    private List<ComboGroup> comboGroups;

    public ComboData(@NotNull List<ComboGroup> comboGroups) {
        this.comboGroups = comboGroups;
    }

    @NotNull
    public List<ComboGroup> getComboGroups() {
        return comboGroups;
    }

    public void setComboGroups(@NotNull List<ComboGroup> comboGroups) {
        this.comboGroups = comboGroups;
    }

    @Override
    public String toString() {
        return "ComboData{" +
                "comboGroups=" + comboGroups +
                '}';
    }
}