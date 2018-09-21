package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.dashboard;

import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieData;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieEntry;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadePergunta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 21/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class DashboardChecklistOsComponentsCreator {

    private DashboardChecklistOsComponentsCreator() {
        throw new IllegalStateException(DashboardChecklistOsComponentsCreator.class.getSimpleName() + " cannot be " +
                "instatiated!");
    }

    @NotNull
    static PieChartComponent createQtdItensOsByPrioridade(
            @NotNull final ComponentDataHolder component,
            @NotNull final Map<PrioridadePergunta, Integer> itensOsPrioridade) {
        final List<PieEntry> entries = new ArrayList<>(itensOsPrioridade.size());
        itensOsPrioridade.forEach((statusPneu, integer) -> entries.add(PieEntry.create(
                statusPneu.getSliceDescription(),
                integer,
                String.valueOf(integer),
                statusPneu.getSliceColor())));
        final PieData pieData = new PieData(entries);
        return PieChartComponent.createDefault(component, pieData);
    }
}