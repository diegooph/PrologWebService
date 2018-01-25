package br.com.zalf.prolog.webservice.dashboard.components.combochart;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ComboGroup {

    @NotNull
    private String groupDescription;
    @NotNull
    private List<ComboEntry> comboEntries;

    public ComboGroup(@NotNull String groupDescription,
                      @NotNull List<ComboEntry> comboEntries) {
        this.groupDescription = groupDescription;
        this.comboEntries = comboEntries;
    }

    @NotNull
    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(@NotNull String groupDescription) {
        this.groupDescription = groupDescription;
    }

    @NotNull
    public List<ComboEntry> getComboEntries() {
        return comboEntries;
    }

    public void setComboEntries(@NotNull List<ComboEntry> comboEntries) {
        this.comboEntries = comboEntries;
    }

    @Override
    public String toString() {
        return "ComboGroup{" +
                "groupDescription='" + groupDescription + '\'' +
                ", comboEntries=" + comboEntries +
                '}';
    }
}
