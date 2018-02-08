package br.com.zalf.prolog.webservice.dashboard.components.densitychart;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class DensityGroup {

    @NotNull
    private List<DensityEntry> densityEntries;
    @NotNull
    private String descricaoGrupo;

    public DensityGroup(@NotNull List<DensityEntry> densityEntries, @NotNull String descricaoGrupo) {
        this.densityEntries = densityEntries;
        this.descricaoGrupo = descricaoGrupo;
    }

    @NotNull
    public List<DensityEntry> getDensityEntries() {
        return densityEntries;
    }

    public void setDensityEntries(@NotNull List<DensityEntry> densityEntries) {
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
        return "DensityGroup{" +
                "densityEntries=" + densityEntries +
                ", descricaoGrupo='" + descricaoGrupo + '\'' +
                '}';
    }
}