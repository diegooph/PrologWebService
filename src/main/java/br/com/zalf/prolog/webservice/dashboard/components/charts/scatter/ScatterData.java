package br.com.zalf.prolog.webservice.dashboard.components.charts.scatter;

import br.com.zalf.prolog.webservice.dashboard.base.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ScatterData extends Data {
    @NotNull
    private List<ScatterGroup> scatterGroup;

    public ScatterData(@NotNull List<ScatterGroup> scatterGroup) {
        this.scatterGroup = scatterGroup;
    }

    @NotNull
    public List<ScatterGroup> getScatterGroup() {
        return scatterGroup;
    }

    public void setScatterGroup(@NotNull List<ScatterGroup> scatterGroup) {
        this.scatterGroup = scatterGroup;
    }

    @Override
    public String toString() {
        return "ScatterData{" +
                "scatterGroup=" + scatterGroup +
                '}';
    }
}