package br.com.zalf.prolog.webservice.frota.pneu.dashboard;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import br.com.zalf.prolog.webservice.dashboard.components.barchart.BarData;
import br.com.zalf.prolog.webservice.dashboard.components.barchart.BarEntry;
import br.com.zalf.prolog.webservice.dashboard.components.barchart.VerticalBarChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.combochart.ComboData;
import br.com.zalf.prolog.webservice.dashboard.components.combochart.ComboEntry;
import br.com.zalf.prolog.webservice.dashboard.components.combochart.ComboGroup;
import br.com.zalf.prolog.webservice.dashboard.components.combochart.VerticalComboChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.densitychart.DensityChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.densitychart.DensityData;
import br.com.zalf.prolog.webservice.dashboard.components.densitychart.DensityEntry;
import br.com.zalf.prolog.webservice.dashboard.components.densitychart.DensityGroup;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieData;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieEntry;
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
                .withLabelEixoX(component.labelEixoX)
                .withLabelEixoY(component.labelEixoY)
                .withComboData(comboData)
                .build();
    }

    @NotNull
    static VerticalBarChartComponent createServicosEmAbertoByTipo(@NotNull final ComponentDataHolder component,
                                                                  @NotNull final Map<TipoServico, Integer> servicosAbertosPorTipo) {
        final List<BarEntry> entries = new ArrayList<>(servicosAbertosPorTipo.size());

        if (!servicosAbertosPorTipo.isEmpty()) {
            // Não utilizamos um for para garantir que as barras do gráfico sempre irão na mesma ordem de exibição.
            // Calibragem.
            entries.add(BarEntry.create(
                    servicosAbertosPorTipo.get(TipoServico.CALIBRAGEM),
                    String.valueOf(servicosAbertosPorTipo.get(TipoServico.CALIBRAGEM)),
                    0,
                    TipoServico.CALIBRAGEM.getLegend(),
                    null));
            // Inspeção.
            entries.add(BarEntry.create(
                    servicosAbertosPorTipo.get(TipoServico.INSPECAO),
                    String.valueOf(servicosAbertosPorTipo.get(TipoServico.INSPECAO)),
                    1,
                    TipoServico.INSPECAO.getLegend(),
                    null));
            // Movimentação.
            entries.add(BarEntry.create(
                    servicosAbertosPorTipo.get(TipoServico.MOVIMENTACAO),
                    String.valueOf(servicosAbertosPorTipo.get(TipoServico.MOVIMENTACAO)),
                    2,
                    TipoServico.MOVIMENTACAO.getLegend(),
                    null));
        }

        final BarData barData = new BarData(entries);
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
                Color.RED));
        entries.add(PieEntry.create(
                "Placas no prazo",
                statusPlacasAfericao.getQtdPlacasAfericaoVencida(),
                String.valueOf(statusPlacasAfericao.getQtdPlacasAfericaoVencida()),
                Color.GREEN));
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
    static DensityChartComponent createMenorSulcoEPressaoPneus(@NotNull final ComponentDataHolder component,
                                                               @NotNull final List<SulcoPressao> valores) {
        final List<DensityEntry> entries = new ArrayList<>(valores.size());
        valores.forEach(sulcoPressao -> entries.add(DensityEntry.create(
                sulcoPressao.getValorPressao(),
                String.valueOf(sulcoPressao.getValorPressao()),
                sulcoPressao.getValorSulco(),
                String.valueOf(sulcoPressao.getValorSulco()))));

        final DensityGroup group = new DensityGroup(entries, "Pneus");
        final List<DensityGroup> groups = new ArrayList<>(1);
        groups.add(group);
        final DensityData data = new DensityData(groups);

        return new DensityChartComponent.Builder()
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
                .withDensityData(data)
                .build();
    }

    @NotNull
    static VerticalBarChartComponent createMediaTempoConsertoServicoPorTipo(@NotNull final ComponentDataHolder component,
                                                                            @NotNull final Map<TipoServico, Integer> tipoServicoHorasConserto) {
        final List<BarEntry> entries = new ArrayList<>(tipoServicoHorasConserto.size());

        if (!tipoServicoHorasConserto.isEmpty()) {
            // Não utilizamos um for para garantir que as barras do gráfico sempre irão na mesma ordem de exibição.
            // Calibragem.
            entries.add(BarEntry.create(
                    tipoServicoHorasConserto.get(TipoServico.CALIBRAGEM),
                    String.valueOf(tipoServicoHorasConserto.get(TipoServico.CALIBRAGEM)),
                    0,
                    TipoServico.CALIBRAGEM.getLegend(),
                    null));
            // Inspeção.
            entries.add(BarEntry.create(
                    tipoServicoHorasConserto.get(TipoServico.INSPECAO),
                    String.valueOf(tipoServicoHorasConserto.get(TipoServico.INSPECAO)),
                    1,
                    TipoServico.INSPECAO.getLegend(),
                    null));
            // Movimentação.
            entries.add(BarEntry.create(
                    tipoServicoHorasConserto.get(TipoServico.MOVIMENTACAO),
                    String.valueOf(tipoServicoHorasConserto.get(TipoServico.MOVIMENTACAO)),
                    2,
                    TipoServico.MOVIMENTACAO.getLegend(),
                    null));
        }

        final BarData barData = new BarData(entries);
        return VerticalBarChartComponent.createDefault(component, barData, null);
    }
}