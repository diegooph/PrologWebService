package br.com.zalf.prolog.webservice.frota.pneu.dashboard;

import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import br.com.zalf.prolog.webservice.dashboard.components.combochart.ComboData;
import br.com.zalf.prolog.webservice.dashboard.components.combochart.ComboEntry;
import br.com.zalf.prolog.webservice.dashboard.components.combochart.ComboGroup;
import br.com.zalf.prolog.webservice.dashboard.components.combochart.VerticalComboChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieData;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieEntry;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.QuantidadeAfericao;
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

    @NotNull
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
                .withTitulo(component.tituloComponente)
                .withSubtitulo(component.subtituloComponente)
                .withDescricao(component.descricaoComponente)
                .withCodTipoComponente(component.codigoTipoComponente)
                .withUrlEndpointDados(component.urlEndpointDados)
                .withQtdBlocosHorizontais(component.qtdBlocosHorizontais)
                .withQtdBlocosVerticais(component.qtdBlocosVerticais)
                .withOrdemExibicao(component.ordemExibicao)
                .withPieData(pieData)
                .build();
    }

    @NotNull
    static QuantidadeItemComponent createQtdPneusPressaoIncorreta(@NotNull final ComponentDataHolder component,
                                                                  final int qtdPneusPressaoIncorreta) {
        return QuantidadeItemComponent.createDefault(component, qtdPneusPressaoIncorreta);
    }

    @NotNull
    static VerticalComboChartComponent createQtdAfericoesUltimaSemana(@NotNull final ComponentDataHolder component,
                                                                      @NotNull final List<QuantidadeAfericao> quantidadeAfericoes) {
        final List<ComboGroup> groups = new ArrayList<>(quantidadeAfericoes.size());
        quantidadeAfericoes.forEach(quantidadeAfericao -> {
            final List<ComboEntry> entries = new ArrayList<>(3 /* 3 tipos de aferição */);
            // Sulco.
            entries.add(ComboEntry.create(
                    quantidadeAfericao.getQtdAfericoesSulco(),
                    String.valueOf(quantidadeAfericao.getQtdAfericoesSulco()),
                    0));
            // Pressão
            entries.add(ComboEntry.create(
                    quantidadeAfericao.getQtdAfericoesPressao(),
                    String.valueOf(quantidadeAfericao.getQtdAfericoesPressao()),
                    1));
            // Sulco e Pressão
            entries.add(ComboEntry.create(
                    quantidadeAfericao.getQtdAfericoesSulcoPressao(),
                    String.valueOf(quantidadeAfericao.getQtdAfericoesSulcoPressao()),
                    2));

            groups.add(ComboGroup.create(
                    quantidadeAfericao.getData().toString(),
                    entries));
        });

        final List<String> legendas = new ArrayList<>(3);
        legendas.add(TipoAfericao.SULCO.getLegibleString());
        legendas.add(TipoAfericao.PRESSAO.getLegibleString());
        legendas.add(TipoAfericao.SULCO_PRESSAO.getLegibleString());

        final ComboData comboData = new ComboData(groups);
        return new VerticalComboChartComponent.Builder()
                .withTitulo(component.tituloComponente)
                .withSubtitulo(component.subtituloComponente)
                .withDescricao(component.descricaoComponente)
                .withCodTipoComponente(component.codigoTipoComponente)
                .withUrlEndpointDados(component.urlEndpointDados)
                .withQtdBlocosHorizontais(component.qtdBlocosHorizontais)
                .withQtdBlocosVerticais(component.qtdBlocosVerticais)
                .withOrdemExibicao(component.ordemExibicao)
                .withLegendas(legendas)
                .withLabelEixoX(component.labelEixoX)
                .withLabelEixoY(component.labelEixoY)
                .withComboData(comboData)
                .build();
    }
}