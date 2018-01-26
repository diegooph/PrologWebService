package br.com.zalf.prolog.webservice.dashboard.components.densitychart;

import br.com.zalf.prolog.webservice.dashboard.base.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class DensityData extends Data {
    @NotNull
    private List<DensityGroup> densityGroup;

    public DensityData(@NotNull List<DensityGroup> densityGroup) {
        this.densityGroup = densityGroup;
    }

    @NotNull
    public List<DensityGroup> getDensityGroup() {
        return densityGroup;
    }

    public void setDensityGroup(@NotNull List<DensityGroup> densityGroup) {
        this.densityGroup = densityGroup;
    }

    @Override
    public String toString() {
        return "DensityData{" +
                "densityGroup=" + densityGroup +
                '}';
    }
}