package br.com.zalf.prolog.webservice.seguranca.relato.dashboard;

import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieData;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieEntry;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.SliceValueMode;
import br.com.zalf.prolog.webservice.seguranca.relato.model.RelatoPendente;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.seguranca.relato.model.StatusRelato.PENDENTE_CLASSIFICACAO;
import static br.com.zalf.prolog.webservice.seguranca.relato.model.StatusRelato.PENDENTE_FECHAMENTO;

/**
 * Created on 2/8/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class DashboardRelatoComponentsCreator {

    private DashboardRelatoComponentsCreator() {
        throw new IllegalStateException(DashboardRelatoComponentsCreator.class.getSimpleName() + " cannot be instatiated!");
    }


    @NotNull
    static QuantidadeItemComponent createQtdRelatosRealizadosHoje(@NotNull final ComponentDataHolder component,
                                                                  final int qtdRelatosRealizadosHoje) {
        return QuantidadeItemComponent.createDefault(
                component,
                String.valueOf(qtdRelatosRealizadosHoje),
                "relatos realizados hoje");
    }

    @NotNull
    static PieChartComponent createQtdRelatosPendentesByStatusInterval30days(
            @NotNull final ComponentDataHolder component,
            @NotNull final RelatoPendente qtdRelatosPendentes) {
        final List<PieEntry> entries = new ArrayList<>(2 /* Relatos pendentes. */);
        entries.add(PieEntry.create(
                "Pendentes classificação",
                qtdRelatosPendentes.getQtdRelatosPendentesClassificacao(),
                String.valueOf(qtdRelatosPendentes.getQtdRelatosPendentesClassificacao()),
                PENDENTE_CLASSIFICACAO.getSliceColor()));
        entries.add(PieEntry.create(
                "Pendentes fechamento",
                qtdRelatosPendentes.getQtdRelatosPendentesFechamento(),
                String.valueOf(qtdRelatosPendentes.getQtdRelatosPendentesFechamento()),
                PENDENTE_FECHAMENTO.getSliceColor()));
        final PieData pieData = new PieData(entries);
        return PieChartComponent.createDefault(component, pieData, SliceValueMode.VALUE_REPRESENTATION);
    }
}