package br.com.zalf.prolog.webservice.commons.dashboard;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PieData {

    @NotNull
    private List<PieEntry> pieEntries;

    public PieData(@NotNull List<PieEntry> pieEntries) {
        this.pieEntries = pieEntries;
    }

    @NotNull
    public List<PieEntry> getPieEntries() {
        return pieEntries;
    }

    public void setPieEntries(@NotNull List<PieEntry> pieEntries) {
        this.pieEntries = pieEntries;
    }

    @Override
    public String toString() {
        return "PieData{" +
                "pieEntries=" + pieEntries +
                '}';
    }
}
