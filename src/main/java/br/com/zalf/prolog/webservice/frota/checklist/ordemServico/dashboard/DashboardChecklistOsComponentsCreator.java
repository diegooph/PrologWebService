package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.dashboard;

import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieData;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieEntry;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.SliceValueMode;
import br.com.zalf.prolog.webservice.dashboard.components.table.*;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadePergunta;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.PlacaItensOsAbertos;
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
        return PieChartComponent.createDefault(component, pieData, SliceValueMode.VALUE_REPRESENTATION);
    }

    @NotNull
    static TableComponent createPlacasMaiorQtdItensOsAbertos(
            @NotNull final ComponentDataHolder component,
            @NotNull final List<PlacaItensOsAbertos> itensOsAbertos) {
        // Header.
        final List<TableItemHeader> itemHeaders = new ArrayList<>(3);
        itemHeaders.add(new TableItemHeader("Unidade", null));
        itemHeaders.add(new TableItemHeader("Placas", null));
        itemHeaders.add(new TableItemHeader("Itens O.S. Abertos", null));
        final TableHeader tableHeader = new TableHeader(itemHeaders);

        // Linhas.
        final List<TableLine> lines = new ArrayList<>();
        itensOsAbertos.forEach(placaItens -> {
            // Colunas.
            final List<TableColumn> columns = new ArrayList<>(3);
            columns.add(new TableColumn(placaItens.getNomeUnidadePlaca()));
            columns.add(new TableColumn(placaItens.getPlaca()));
            columns.add(new TableColumn(String.valueOf(placaItens.getQtdItensOsAbertosPlaca())));
            lines.add(new TableLine(columns));
        });

        final TableData tableData = new TableData(lines);
        return TableComponent.createDefault(component, tableHeader, tableData);
    }
}