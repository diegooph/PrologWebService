package br.com.zalf.prolog.webservice.dashboard.components.charts.scatter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ScatterGroup {

    @NotNull
    private List<ScatterEntry> densityEntries;
    @NotNull
    private String descricaoGrupo;

    public ScatterGroup(@NotNull List<ScatterEntry> densityEntries, @NotNull String descricaoGrupo) {
        this.densityEntries = densityEntries;
        this.descricaoGrupo = descricaoGrupo;
    }

    @NotNull
    public List<ScatterEntry> getDensityEntries() {
        return densityEntries;
    }

    public void setDensityEntries(@NotNull List<ScatterEntry> densityEntries) {
        this.densityEntries = densityEntries;
    }

    @NotNull
    public String getDescricaoGrupo() {
        return descricaoGrupo;
    }

    public void setDescricaoGrupo(@NotNull String descricaoGrupo) {
        this.descricaoGrupo = descricaoGrupo;
    }

    @Override
    public String toString() {
        return "ScatterGroup{" +
                "densityEntries=" + densityEntries +
                ", descricaoGrupo='" + descricaoGrupo + '\'' +
                '}';
    }
}