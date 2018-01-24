package br.com.zalf.prolog.webservice.frota.pneu.dashboard;

import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieData;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieEntry;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.StatusPneu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 1/22/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class DashboardPneuComponentsCreator {

    private DashboardPneuComponentsCreator() {
        throw new IllegalStateException(DashboardPneuComponentsCreator.class.getSimpleName() + " cannot be instatiated!");
    }

    static PieChartComponent createQtdPneusByStatus(@NotNull final ComponentDataHolder component,
                                                    @NotNull final Map<StatusPneu, Integer> qtdPneusStatus) {
        final List<PieEntry> entries = new ArrayList<>(qtdPneusStatus.size());
        qtdPneusStatus.forEach((statusPneu, integer) -> entries.add(PieEntry.create(
                statusPneu.getSliceDescription(),
                integer,
                String.valueOf(integer),
                statusPneu.getSliceColor())));
        final PieData pieData = new PieData(entries);
        return new PieChartComponent.Builder()
                .withTitulo("Quantidade de pneus por status")
                .withDescricao("Este gráfico mostra quantos pneus existem em cada estado")
                .withCodTipoComponente(component.codigoTipoComponente)
                .withUrlEndpointDados(component.urlEndpointDados)
                .withQtdBlocosHorizontais(component.qtdBlocosHorizontais)
                .withQtdBlocosVerticais(component.qtdBlocosVerticais)
                .withOrdemExibicao(component.ordemExibicao)
                .withPieData(pieData)
                .build();
    }
}