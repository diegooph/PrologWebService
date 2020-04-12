package br.com.zalf.prolog.webservice.frota.socorrorota.dashboard;

import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieData;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieEntry;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.SliceValueMode;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.StatusSocorroRota;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 2020-03-31
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class DashboardSocorroRotaComponentsCreator {

    private DashboardSocorroRotaComponentsCreator() {
        throw new IllegalStateException(DashboardSocorroRotaComponentsCreator.class.getSimpleName() + " cannot be " +
                "instatiated!");
    }

    @NotNull
    static PieChartComponent createSocorrosPorStatus(@NotNull final ComponentDataHolder component,
                                                     @NotNull final Map<StatusSocorroRota, Integer> qtdSocorroRotaStatus) {
        final List<PieEntry> entries = new ArrayList<>(qtdSocorroRotaStatus.size());
        qtdSocorroRotaStatus.forEach((statusSocorroRota, integer) -> entries.add(PieEntry.create(
                statusSocorroRota.getSliceDescription(),
                integer,
                String.valueOf(integer),
                statusSocorroRota.getSliceColor())));
        final PieData pieData = new PieData(entries);
        return PieChartComponent.createDefault(component, pieData, SliceValueMode.SLICE_PERCENTAGE);
    }

}
