package br.com.zalf.prolog.webservice.frota.pneu.dashboard;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.bar.BarData;
import br.com.zalf.prolog.webservice.dashboard.components.charts.bar.BarEntry;
import br.com.zalf.prolog.webservice.dashboard.components.charts.bar.BarGroup;
import br.com.zalf.prolog.webservice.dashboard.components.charts.bar.VerticalBarChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.combo.ComboData;
import br.com.zalf.prolog.webservice.dashboard.components.charts.combo.ComboEntry;
import br.com.zalf.prolog.webservice.dashboard.components.charts.combo.ComboGroup;
import br.com.zalf.prolog.webservice.dashboard.components.charts.combo.VerticalComboChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.scatter.ScatterChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.scatter.ScatterData;
import br.com.zalf.prolog.webservice.dashboard.components.charts.scatter.ScatterEntry;
import br.com.zalf.prolog.webservice.dashboard.components.charts.scatter.ScatterGroup;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieData;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieEntry;
import br.com.zalf.prolog.webservice.dashboard.components.table.*;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.QuantidadeAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.StatusPlacasAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.SulcoPressao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.TipoServico;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static br.com.zalf.prolog.webservice.frota.pneu.servico.model.TipoServico.*;

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
        return PieChartComponent.createDefault(component, pieData);
    }

    @NotNull
    static QuantidadeItemComponent createQtdPneusPressaoIncorreta(@NotNull final ComponentDataHolder component,
                                                                  final int qtdPneusPressaoIncorreta) {
        return QuantidadeItemComponent.createDefault(
                component,
                String.valueOf(qtdPneusPressaoIncorreta),
                "pneus com pressão incorreta");
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
                    quantidadeAfericao.getDataFormatada(),
                    entries));
        });

        final List<String> legendas = new ArrayList<>(3);
        legendas.add(TipoAfericao.SULCO.getLegibleString());
        legendas.add(TipoAfericao.PRESSAO.getLegibleString());
        legendas.add(TipoAfericao.SULCO_PRESSAO.getLegibleString());

        final List<Color> colors = new ArrayList<>(3);
        colors.add(TipoAfericao.SULCO.getColor());
        colors.add(TipoAfericao.PRESSAO.getColor());
        colors.add(TipoAfericao.SULCO_PRESSAO.getColor());

        final ComboData comboData = new ComboData(groups);
        return new VerticalComboChartComponent.Builder()
                .withCodigo(component.codigoComponente)
                .withTitulo(component.tituloComponente)
                .withSubtitulo(component.subtituloComponente)
                .withDescricao(component.descricaoComponente)
                .withCodTipoComponente(component.codigoTipoComponente)
                .withUrlEndpointDados(component.urlEndpointDados)
                .withQtdBlocosHorizontais(component.qtdBlocosHorizontais)
                .withQtdBlocosVerticais(component.qtdBlocosVerticais)
                .withOrdemExibicao(component.ordemExibicao)
                .withLegendas(legendas)
                .withEntryColors(colors)
                .withLabelEixoX(component.labelEixoX)
                .withLabelEixoY(component.labelEixoY)
                .withComboData(comboData)
                .build();
    }

    @NotNull
    static VerticalBarChartComponent createServicosEmAbertoByTipo(@NotNull final ComponentDataHolder component,
                                                                  @NotNull final Map<TipoServico, Integer> servicosAbertosPorTipo) {
        final List<BarGroup> groups = new ArrayList<>(servicosAbertosPorTipo.size());

        if (!servicosAbertosPorTipo.isEmpty()) {
            // Não utilizamos um for para garantir que as barras do gráfico sempre irão na mesma ordem de exibição.

            // Calibragem.
            final List<BarEntry> entriesCalibragem = new ArrayList<>(1);
            entriesCalibragem.add(BarEntry.create(
                    servicosAbertosPorTipo.get(CALIBRAGEM),
                    String.valueOf(servicosAbertosPorTipo.get(CALIBRAGEM)),
                    0,
                    null));
            groups.add(new BarGroup(CALIBRAGEM.getLegend(), entriesCalibragem, CALIBRAGEM.getColor()));

            // Inspeção.
            final List<BarEntry> entriesInspecao = new ArrayList<>(1);
            entriesInspecao.add(BarEntry.create(
                    servicosAbertosPorTipo.get(INSPECAO),
                    String.valueOf(servicosAbertosPorTipo.get(INSPECAO)),
                    1,
                    null));
            groups.add(new BarGroup(INSPECAO.getLegend(), entriesInspecao, INSPECAO.getColor()));

            // Movimentação.
            final List<BarEntry> entriesMovimentacao = new ArrayList<>(1);
            entriesMovimentacao.add(BarEntry.create(
                    servicosAbertosPorTipo.get(MOVIMENTACAO),
                    String.valueOf(servicosAbertosPorTipo.get(MOVIMENTACAO)),
                    2,
                    null));
            groups.add(new BarGroup(MOVIMENTACAO.getLegend(), entriesMovimentacao, MOVIMENTACAO.getColor()));
        }

        final BarData barData = new BarData(groups);
        return VerticalBarChartComponent.createDefault(component, barData, null);
    }

    @NotNull
    static PieChartComponent createStatusPlacaAfericao(@NotNull final ComponentDataHolder component,
                                                       @NotNull final StatusPlacasAfericao statusPlacasAfericao) {
        final List<PieEntry> entries = new ArrayList<>(2 /* Aferições vencidas e no prazo. */);
        entries.add(PieEntry.create(
                "Placas vencidas",
                statusPlacasAfericao.getQtdPlacasAfericaoVencida(),
                String.valueOf(statusPlacasAfericao.getQtdPlacasAfericaoVencida()),
                Color.fromHex("#EC441B")));
        entries.add(PieEntry.create(
                "Placas no prazo",
                statusPlacasAfericao.getQtdPlacasAfericaoNoPrazo(),
                String.valueOf(statusPlacasAfericao.getQtdPlacasAfericaoNoPrazo()),
                Color.fromHex("#15C41F")));
        final PieData pieData = new PieData(entries);
        return PieChartComponent.createDefault(component, pieData);
    }

    @NotNull
    static TableComponent createPlacasComPneuAbaixoLimiteMilimetragem(@NotNull final ComponentDataHolder component,
                                                                      @NotNull final Map<String, Integer> placasQtdPneus) {
        // Header.
        final List<TableItemHeader> itemHeaders = new ArrayList<>(2 /* Placa e quantidade de pneus. */);
        itemHeaders.add(new TableItemHeader("Placa", null));
        itemHeaders.add(new TableItemHeader("Pneus com problema", null));
        final TableHeader tableHeader = new TableHeader(itemHeaders);

        // Linhas.
        final List<TableLine> lines = TableComponent.createLinesFromMap(placasQtdPneus);

        final TableData tableData = new TableData(lines);
        return TableComponent.createDefault(component, tableHeader, tableData);
    }

    @NotNull
    static TableComponent createQtdKmRodadoComServicoEmAberto(@NotNull final ComponentDataHolder component,
                                                              @NotNull final Map<String, Integer> placasQtdKm) {
        // Header.
        final List<TableItemHeader> itemHeaders = new ArrayList<>(2 /* Placa e total KM. */);
        itemHeaders.add(new TableItemHeader("Placa", null));
        itemHeaders.add(new TableItemHeader("Total KM percorrido", null));
        final TableHeader tableHeader = new TableHeader(itemHeaders);

        // Linhas.
        final List<TableLine> lines = TableComponent.createLinesFromMap(placasQtdKm);

        final TableData tableData = new TableData(lines);
        return TableComponent.createDefault(component, tableHeader, tableData);
    }

    @NotNull
    static ScatterChartComponent createMenorSulcoEPressaoPneus(@NotNull final ComponentDataHolder component,
                                                               @NotNull final List<SulcoPressao> valores) {
        final List<ScatterEntry> entries = new ArrayList<>(valores.size());
        valores.forEach(sulcoPressao -> entries.add(ScatterEntry.create(
                sulcoPressao.getValorPressao(),
                String.valueOf(sulcoPressao.getValorPressao()),
                sulcoPressao.getValorSulco(),
                String.valueOf(sulcoPressao.getValorSulco()))));

        final ScatterGroup group = new ScatterGroup(entries, "Pneus", Color.fromHex("#C12552"));
        final List<ScatterGroup> groups = new ArrayList<>(1);
        groups.add(group);
        final ScatterData data = new ScatterData(groups);

        return new ScatterChartComponent.Builder()
                .withCodigo(component.codigoComponente)
                .withTitulo(component.tituloComponente)
                .withSubtitulo(component.subtituloComponente)
                .withDescricao(component.descricaoComponente)
                .withCodTipoComponente(component.codigoTipoComponente)
                .withUrlEndpointDados(component.urlEndpointDados)
                .withQtdBlocosHorizontais(component.qtdBlocosHorizontais)
                .withQtdBlocosVerticais(component.qtdBlocosVerticais)
                .withOrdemExibicao(component.ordemExibicao)
                .withLabelEixoX(component.labelEixoX)
                .withLabelEixoY(component.labelEixoY)
                .withScatterData(data)
                .build();
    }

    @NotNull
    static VerticalBarChartComponent createMediaTempoConsertoServicoPorTipo(@NotNull final ComponentDataHolder component,
                                                                            @NotNull final Map<TipoServico, Integer> tipoServicoHorasConserto) {
        final List<BarGroup> groups = new ArrayList<>(tipoServicoHorasConserto.size());

        if (!tipoServicoHorasConserto.isEmpty()) {
            // Não utilizamos um for para garantir que as barras do gráfico sempre irão na mesma ordem de exibição.

            // Calibragem.
            final List<BarEntry> entriesCalibragem = new ArrayList<>(1);
            entriesCalibragem.add(BarEntry.create(
                    tipoServicoHorasConserto.get(CALIBRAGEM),
                    String.valueOf(tipoServicoHorasConserto.get(CALIBRAGEM)),
                    0,
                    null));
            groups.add(new BarGroup(CALIBRAGEM.getLegend(), entriesCalibragem, CALIBRAGEM.getColor()));

            // Inspeção.
            final List<BarEntry> entriesInspecao = new ArrayList<>(1);
            entriesInspecao.add(BarEntry.create(
                    tipoServicoHorasConserto.get(INSPECAO),
                    String.valueOf(tipoServicoHorasConserto.get(INSPECAO)),
                    1,
                    null));
            groups.add(new BarGroup(INSPECAO.getLegend(), entriesInspecao, INSPECAO.getColor()));

            // Movimentação.
            final List<BarEntry> entriesMovimentacao = new ArrayList<>(1);
            entriesMovimentacao.add(BarEntry.create(
                    tipoServicoHorasConserto.get(MOVIMENTACAO),
                    String.valueOf(tipoServicoHorasConserto.get(MOVIMENTACAO)),
                    2,
                    null));
            groups.add(new BarGroup(MOVIMENTACAO.getLegend(), entriesMovimentacao, MOVIMENTACAO.getColor()));
        }

        final BarData barData = new BarData(groups);
        return VerticalBarChartComponent.createDefault(component, barData, null);
    }

    static QuantidadeItemComponent createQtdPneusCadastrados(@NotNull final ComponentDataHolder component,
                                                             @NotNull final Map<StatusPneu, Integer> qtPneusByStatus) {
        qtPneusByStatus.remove(StatusPneu.DESCARTE);
        return QuantidadeItemComponent.createDefault(
                component,
                String.valueOf(qtPneusByStatus.size()),
                "pneus cadastrados");
    }
}