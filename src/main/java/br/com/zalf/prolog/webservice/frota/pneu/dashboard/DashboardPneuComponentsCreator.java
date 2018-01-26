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
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieData;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieEntry;
import br.com.zalf.prolog.webservice.dashboard.components.table.*;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.QuantidadeAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.StatusPlacasAfericao;
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

    @NotNull
    static VerticalBarChartComponent createServicosEmAbertoByTipo(@NotNull final ComponentDataHolder component,
                                                                  @NotNull final Map<TipoServico, Integer> servicosAbertosPorTipo) {
        final List<BarEntry> entries = new ArrayList<>(servicosAbertosPorTipo.size());

        // Não utilizamos um for para garantir que as barras do gráfico sempre irão na mesma ordem de exibição.
        // Calibragem.
        entries.add(BarEntry.create(
                servicosAbertosPorTipo.get(TipoServico.CALIBRAGEM),
                String.valueOf(servicosAbertosPorTipo.get(TipoServico.CALIBRAGEM)),
                0,
                TipoServico.CALIBRAGEM.asString(),
                null));
        // Inspeção.
        entries.add(BarEntry.create(
                servicosAbertosPorTipo.get(TipoServico.INSPECAO),
                String.valueOf(servicosAbertosPorTipo.get(TipoServico.INSPECAO)),
                1,
                TipoServico.INSPECAO.asString(),
                null));
        // Movimentação.
        entries.add(BarEntry.create(
                servicosAbertosPorTipo.get(TipoServico.MOVIMENTACAO),
                String.valueOf(servicosAbertosPorTipo.get(TipoServico.MOVIMENTACAO)),
                2,
                TipoServico.MOVIMENTACAO.asString(),
                null));

        final BarData barData = new BarData(entries);
        return new VerticalBarChartComponent.Builder()
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
                .withBarData(barData)
                .build();
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
    static TableComponent createPlacasComPneuAbaixoLimiteMilimetragem(@NotNull final ComponentDataHolder component,
                                                                      @NotNull final Map<String, Integer> placasQtdPneus) {
        // Header.
        final List<TableItemHeader> itemHeaders = new ArrayList<>(2 /* Placa e quantidade de pneus. */);
        itemHeaders.add(new TableItemHeader("Placa", null));
        itemHeaders.add(new TableItemHeader("Quantidade de pneus", null));
        final TableHeader tableHeader = new TableHeader(itemHeaders);

        // Linhas.
        final List<TableLine> lines = new ArrayList<>(placasQtdPneus.size());
        placasQtdPneus.forEach((placa, quantidadePneus) -> {
            // Colunas.
            final List<TableColumn> columns = new ArrayList<>(2);
            columns.add(new TableColumn(placa));
            columns.add(new TableColumn(String.valueOf(quantidadePneus)));
            lines.add(new TableLine(columns));
        });

        final TableData tableData = new TableData(lines);
        return new TableComponent.Builder()
                .withTitulo(component.tituloComponente)
                .withSubtitulo(component.subtituloComponente)
                .withDescricao(component.descricaoComponente)
                .withCodTipoComponente(component.codigoTipoComponente)
                .withUrlEndpointDados(component.urlEndpointDados)
                .withQtdBlocosHorizontais(component.qtdBlocosHorizontais)
                .withQtdBlocosVerticais(component.qtdBlocosVerticais)
                .withOrdemExibicao(component.ordemExibicao)
                .withTableHeader(tableHeader)
                .withTableData(tableData)
                .build();
    }
}