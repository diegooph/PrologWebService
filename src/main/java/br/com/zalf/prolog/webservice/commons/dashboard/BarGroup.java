package br.com.zalf.prolog.webservice.commons.dashboard;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class BarGroup {

    @NotNull
    private String groupDescription;
    @NotNull
    private List<BarEntry> barEntries;

    public BarGroup(@NotNull String groupDescription,
                    @NotNull List<BarEntry> barEntries) {
        this.groupDescription = groupDescription;
        this.barEntries = barEntries;
    }

    @NotNull
    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(@NotNull String groupDescription) {
        this.groupDescription = groupDescription;
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
        return "BarGroup{" +
                "groupDescription='" + groupDescription + '\'' +
                ", barEntries=" + barEntries +
                '}';
    }
}
