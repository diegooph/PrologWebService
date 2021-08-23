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
import br.com.zalf.prolog.webservice.dashboard.components.charts.line.*;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieData;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieEntry;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.SliceValueMode;
import br.com.zalf.prolog.webservice.dashboard.components.charts.scatter.ScatterChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.scatter.ScatterData;
import br.com.zalf.prolog.webservice.dashboard.components.charts.scatter.ScatterEntry;
import br.com.zalf.prolog.webservice.dashboard.components.charts.scatter.ScatterGroup;
import br.com.zalf.prolog.webservice.dashboard.components.table.*;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.QtdDiasAfericoesVencidas;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios._model.QuantidadeAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios._model.StatusPlacasAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios._model.SulcoPressao;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao.*;
import static br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico.*;

/**
 * Created on 1/22/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class DashboardPneuComponentsCreator {

    private DashboardPneuComponentsCreator() {
        throw new IllegalStateException(DashboardPneuComponentsCreator.class.getSimpleName() + " cannot be " +
                                                "instatiated!");
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
        return PieChartComponent.createDefault(component, pieData, SliceValueMode.SLICE_PERCENTAGE);
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
                                                                      @NotNull final List<QuantidadeAfericao>
                                                                              quantidadeAfericoes) {
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
        legendas.add(SULCO.getLegibleString());
        legendas.add(TipoMedicaoColetadaAfericao.PRESSAO.getLegibleString());
        legendas.add(TipoMedicaoColetadaAfericao.SULCO_PRESSAO.getLegibleString());

        final List<Color> colors = new ArrayList<>(3);
        colors.add(SULCO.getColor());
        colors.add(TipoMedicaoColetadaAfericao.PRESSAO.getColor());
        colors.add(TipoMedicaoColetadaAfericao.SULCO_PRESSAO.getColor());

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
                                                                  @NotNull final Map<TipoServico, Integer>
                                                                          servicosAbertosPorTipo) {
        final List<BarGroup> groups = new ArrayList<>(servicosAbertosPorTipo.size());

        if (!servicosAbertosPorTipo.isEmpty()) {
            // Não utilizamos um for para garantir que as barras do gráfico sempre irão na mesma ordem de exibição.

            // Calibragem.
            if (servicosAbertosPorTipo.containsKey(CALIBRAGEM)) {
                final List<BarEntry> entriesCalibragem = new ArrayList<>(1);
                entriesCalibragem.add(BarEntry.create(
                        servicosAbertosPorTipo.get(CALIBRAGEM),
                        String.valueOf(servicosAbertosPorTipo.get(CALIBRAGEM)),
                        0,
                        null));
                groups.add(new BarGroup(CALIBRAGEM.getLegend(), entriesCalibragem, CALIBRAGEM.getColor()));
            }

            // Inspeção.
            if (servicosAbertosPorTipo.containsKey(INSPECAO)) {
                final List<BarEntry> entriesInspecao = new ArrayList<>(1);
                entriesInspecao.add(BarEntry.create(
                        servicosAbertosPorTipo.get(INSPECAO),
                        String.valueOf(servicosAbertosPorTipo.get(INSPECAO)),
                        1,
                        null));
                groups.add(new BarGroup(INSPECAO.getLegend(), entriesInspecao, INSPECAO.getColor()));
            }

            // Movimentação.
            if (servicosAbertosPorTipo.containsKey(MOVIMENTACAO)) {
                final List<BarEntry> entriesMovimentacao = new ArrayList<>(1);
                entriesMovimentacao.add(BarEntry.create(
                        servicosAbertosPorTipo.get(MOVIMENTACAO),
                        String.valueOf(servicosAbertosPorTipo.get(MOVIMENTACAO)),
                        2,
                        null));
                groups.add(new BarGroup(MOVIMENTACAO.getLegend(), entriesMovimentacao, MOVIMENTACAO.getColor()));
            }
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
        entries.add(PieEntry.create(
                "Placas nunca aferidas",
                statusPlacasAfericao.getQtdPlacasNuncaAferidas(),
                String.valueOf(statusPlacasAfericao.getQtdPlacasNuncaAferidas()),
                Color.fromHex("#C5C5C5")));
        final PieData pieData = new PieData(entries);
        return PieChartComponent.createDefault(component, pieData, SliceValueMode.SLICE_PERCENTAGE);
    }

    @NotNull
    static TableComponent createPlacasComPneuAbaixoLimiteMilimetragem(@NotNull final ComponentDataHolder component,
                                                                      @NotNull final Map<String, Integer>
                                                                              placasQtdPneus) {
        // Header.
        final List<TableItemHeader> itemHeaders = new ArrayList<>(2 /* Placa e quantidade de pneus. */);
        itemHeaders.add(new TableItemHeader("Placa", null));
        itemHeaders.add(new TableItemHeader("Pneus com problema", null));
        final TableHeader tableHeader = new TableHeader(itemHeaders);

        // Linhas.
        final List<TableLine> lines = TableComponent.createLinesFromMap(placasQtdPneus);

        final TableData tableData = new TableData(lines);
        return TableComponent.createDefault(component, tableHeader, tableData, null);
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
        return TableComponent.createDefault(component, tableHeader, tableData, null);
    }

    @NotNull
    static ScatterChartComponent createMenorSulcoEPressaoPneus(@NotNull final ComponentDataHolder component,
                                                               @NotNull final List<SulcoPressao> valores) {
        final List<ScatterEntry> entries = new ArrayList<>(valores.size());
        for (final SulcoPressao sulcoPressao : valores) {
            final String infoEntry = String.format("Pneu: %s\nSulco: %s\nPressão: %s",
                                                   sulcoPressao.getCodPneuCliente(),
                                                   String.valueOf(sulcoPressao.getValorSulco()),
                                                   String.valueOf(sulcoPressao.getValorPressao()));
            entries.add(ScatterEntry.create(
                    sulcoPressao.getValorPressao(),
                    String.valueOf(sulcoPressao.getValorPressao()),
                    sulcoPressao.getValorSulco(),
                    String.valueOf(sulcoPressao.getValorSulco()),
                    infoEntry));
        }

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
    static VerticalBarChartComponent createMediaTempoConsertoServicoPorTipo(@NotNull final ComponentDataHolder
                                                                                    component,
                                                                            @NotNull final Map<TipoServico, Integer>
                                                                                    tipoServicoHorasConserto) {
        final List<BarGroup> groups = new ArrayList<>(tipoServicoHorasConserto.size());

        if (!tipoServicoHorasConserto.isEmpty()) {
            // Não utilizamos um for para garantir que as barras do gráfico sempre irão na mesma ordem de exibição.

            // Calibragem.
            if (tipoServicoHorasConserto.containsKey(CALIBRAGEM)) {
                final List<BarEntry> entriesCalibragem = new ArrayList<>(1);
                entriesCalibragem.add(BarEntry.create(
                        tipoServicoHorasConserto.get(CALIBRAGEM),
                        String.valueOf(tipoServicoHorasConserto.get(CALIBRAGEM)),
                        0,
                        null));
                groups.add(new BarGroup(CALIBRAGEM.getLegend(), entriesCalibragem, CALIBRAGEM.getColor()));
            }

            // Inspeção.
            if (tipoServicoHorasConserto.containsKey(INSPECAO)) {
                final List<BarEntry> entriesInspecao = new ArrayList<>(1);
                entriesInspecao.add(BarEntry.create(
                        tipoServicoHorasConserto.get(INSPECAO),
                        String.valueOf(tipoServicoHorasConserto.get(INSPECAO)),
                        1,
                        null));
                groups.add(new BarGroup(INSPECAO.getLegend(), entriesInspecao, INSPECAO.getColor()));
            }

            // Movimentação.
            if (tipoServicoHorasConserto.containsKey(MOVIMENTACAO)) {
                final List<BarEntry> entriesMovimentacao = new ArrayList<>(1);
                entriesMovimentacao.add(BarEntry.create(
                        tipoServicoHorasConserto.get(MOVIMENTACAO),
                        String.valueOf(tipoServicoHorasConserto.get(MOVIMENTACAO)),
                        2,
                        null));
                groups.add(new BarGroup(MOVIMENTACAO.getLegend(), entriesMovimentacao, MOVIMENTACAO.getColor()));
            }
        }

        final BarData barData = new BarData(groups);
        return VerticalBarChartComponent.createDefault(component, barData, null);
    }

    static QuantidadeItemComponent createQtdPneusCadastrados(@NotNull final ComponentDataHolder component,
                                                             @NotNull final Map<StatusPneu, Integer> qtPneusByStatus) {
        qtPneusByStatus.remove(StatusPneu.DESCARTE);
        return QuantidadeItemComponent.createDefault(
                component,
                String.valueOf(qtPneusByStatus.values().stream().mapToInt(Number::intValue).sum()),
                "pneus cadastrados");
    }

    @NotNull
    static TableComponent createQuantidadePneusDescartadosPorMotivo(@NotNull final ComponentDataHolder component,
                                                                    @NotNull final Map<String, Integer> qtdMotivosDescarte) {
        // Header.
        final List<TableItemHeader> itemHeaders = new ArrayList<>(2 /* Motivo e quantidade. */);
        itemHeaders.add(new TableItemHeader("Motivo", null));
        itemHeaders.add(new TableItemHeader("Quantidade de pneus", null));
        final TableHeader tableHeader = new TableHeader(itemHeaders);

        // Linhas.
        final List<TableLine> lines = TableComponent.createLinesFromMap(qtdMotivosDescarte);

        final TableData tableData = new TableData(lines);
        return TableComponent.createDefault(component, tableHeader, tableData, null);
    }

    @NotNull
    static TableComponent createQtdDiasAfericoesVencidas(
            @NotNull final ComponentDataHolder component,
            @NotNull final List<QtdDiasAfericoesVencidas> qtdDiasAfericoesVencidas) {
        // Header.
        final List<TableItemHeader> itemHeaders = new ArrayList<>(4);
        itemHeaders.add(new TableItemHeader("Unidade", null));
        itemHeaders.add(new TableItemHeader("Placa", null));
        itemHeaders.add(new TableItemHeader("Identificador Frota", null));
        itemHeaders.add(new TableItemHeader("Dias aferição vencida - sulco", null));
        itemHeaders.add(new TableItemHeader("Dias aferição vencida - pressão", null));
        final TableHeader tableHeader = new TableHeader(itemHeaders);

        // Linhas.
        final List<TableLine> lines = new ArrayList<>();
        qtdDiasAfericoesVencidas.forEach(afericoesVencidas -> {
            final List<TableColumn> columns = new ArrayList<>(4);
            columns.add(new TableColumn(afericoesVencidas.getNomeUnidade()));
            columns.add(new TableColumn(afericoesVencidas.getPlacaVeiculo()));
            columns.add(new TableColumn(afericoesVencidas.getIdentificadorFrota()));

            // Aferição sulco.
            if (afericoesVencidas.isPodeAferirSulco()) {
                if (afericoesVencidas.getQtdDiasAfericaoSulcoVencido().isPresent()) {
                    final int diasVencidos = afericoesVencidas.getQtdDiasAfericaoSulcoVencido().get();
                    if (diasVencidos > 0) {
                        columns.add(new TableColumn(String.valueOf(diasVencidos)));
                    } else {
                        columns.add(new TableColumn("no prazo"));
                    }
                } else {
                    columns.add(new TableColumn("vencido (nunca aferido)"));
                }
            } else {
                columns.add(new TableColumn("bloqueado aferição"));
            }

            // Aferição pressão.
            if (afericoesVencidas.isPodeAferirPressao()) {
                if (afericoesVencidas.getQtdDiasAfericaoPressaoVencida().isPresent()) {
                    final int diasVencidos = afericoesVencidas.getQtdDiasAfericaoPressaoVencida().get();
                    if (diasVencidos > 0) {
                        columns.add(new TableColumn(String.valueOf(diasVencidos)));
                    } else {
                        columns.add(new TableColumn("vencido (nunca aferida)"));
                    }
                } else {
                    columns.add(new TableColumn("vencido (nunca aferida)"));
                }
            } else {
                columns.add(new TableColumn("bloqueado aferição"));
            }

            lines.add(new TableLine(columns));
        });

        final TableData tableData = new TableData(lines);
        final TableFooter tableFooter =
                new TableFooter(new TableItemFooter("Total de placas vencidas:",
                                                    String.valueOf(qtdDiasAfericoesVencidas.size())));
        return TableComponent.createDefault(component, tableHeader, tableData, tableFooter);
    }

    @NotNull
    static HorizontalLineChartComponent getQtdAfericoesRealizadasPorDiaByTipoInterval30Days(
            @NotNull final ComponentDataHolder component,
            @NotNull final List<QuantidadeAfericao> afericaolistsDia) {

        final Map<Double, String> informacoesPontos = new HashMap<>(afericaolistsDia.size());
        final Map<Double, String> representacoesValoresX = new HashMap<>(afericaolistsDia.size());
        final List<LineEntry> entriesSulco = new ArrayList<>();
        final List<LineEntry> entriesPressao = new ArrayList<>();
        final List<LineEntry> entriesSulcoPressao = new ArrayList<>();
        for (int i = 0; i < afericaolistsDia.size(); i++) {
            final QuantidadeAfericao qtdAfericao = afericaolistsDia.get(i);
            final LineEntry sulco = new LineEntry(
                    qtdAfericao.getQtdAfericoesSulco(),
                    i,
                    String.valueOf(qtdAfericao.getQtdAfericoesSulco()),
                    qtdAfericao.getDataFormatada(),
                    null);
            final LineEntry pressao = new LineEntry(
                    qtdAfericao.getQtdAfericoesPressao(),
                    i,
                    String.valueOf(qtdAfericao.getQtdAfericoesPressao()),
                    qtdAfericao.getDataFormatada(),
                    null);
            final LineEntry sulcoPressao = new LineEntry(
                    qtdAfericao.getQtdAfericoesSulcoPressao(),
                    i,
                    String.valueOf(qtdAfericao.getQtdAfericoesSulcoPressao()),
                    qtdAfericao.getDataFormatada(),
                    null);
            entriesSulco.add(sulco);
            entriesPressao.add(pressao);
            entriesSulcoPressao.add(sulcoPressao);

            // Cria a informação do ponto no gráfico em linhas.
            String informacaoPonto = qtdAfericao.getDataFormatada();
            if (qtdAfericao.teveAfericoesRealizadas()) {
                if (qtdAfericao.getQtdAfericoesSulco() > 0) {
                    informacaoPonto = String.format("%s\nSulco: %d", informacaoPonto,
                                                    qtdAfericao.getQtdAfericoesSulco());
                }
                if (qtdAfericao.getQtdAfericoesPressao() > 0) {
                    informacaoPonto = String.format("%s\nPressão: %d", informacaoPonto,
                                                    qtdAfericao.getQtdAfericoesPressao());
                }
                if (qtdAfericao.getQtdAfericoesSulcoPressao() > 0) {
                    informacaoPonto = String.format("%s\nSulco/Pressão: %d", informacaoPonto,
                                                    qtdAfericao.getQtdAfericoesSulcoPressao());
                }
            } else {
                informacaoPonto = String.format("%s\nsem aferições", informacaoPonto);
            }
            informacoesPontos.put((double) i, informacaoPonto);
            representacoesValoresX.put((double) i, qtdAfericao.getDataFormatada());
        }

        final List<LineGroup> groups = new ArrayList<>(3 /* sulco, pressao, pressão e sulco*/);
        final LineGroup groupSulco = new LineGroup("Sulco", entriesSulco, SULCO.getColor());
        final LineGroup groupPressao = new LineGroup("Pressão", entriesPressao, PRESSAO.getColor());
        final LineGroup groupSulcoPressao = new LineGroup("Sulco/Pressão", entriesSulcoPressao,
                                                          SULCO_PRESSAO.getColor());
        groups.add(groupSulco);
        groups.add(groupPressao);
        groups.add(groupSulcoPressao);

        final LineData lineData = new LineData(groups);
        return new HorizontalLineChartComponent.Builder()
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
                .withLineData(lineData)
                .withSelectionLineColor(Color.RED)
                .withRepresentacoesValoresX(representacoesValoresX)
                .withLinesOrientation(LinesOrientation.HORIZONTAL)
                .withInformacoesPontos(informacoesPontos)
                .build();
    }
}