package br.com.zalf.prolog.webservice.dashboard.components.charts.scatter;

import br.com.zalf.prolog.webservice.dashboard.Color;
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
    @NotNull
    private Color corGrupo;

    public ScatterGroup(@NotNull List<ScatterEntry> densityEntries,
                        @NotNull String descricaoGrupo,
                        @NotNull Color groupColor) {
        this.densityEntries = densityEntries;
        this.descricaoGrupo = descricaoGrupo;
        this.corGrupo = groupColor;
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

    @NotNull
    public Color getCorGrupo() {
        return corGrupo;
    }

    public void setCorGrupo(@NotNull Color corGrupo) {
        this.corGrupo = corGrupo;
    }

    @Override
    public String toString() {
        return "ScatterGroup{" +
                "densityEntries=" + densityEntries +
                ", descricaoGrupo='" + descricaoGrupo + '\'' +
                ", corGrupo=" + corGrupo +
                '}';
    }
}