package br.com.zalf.prolog.webservice.frota.checklist.dashboard;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.components.charts.line.*;
import br.com.zalf.prolog.webservice.dashboard.components.table.*;
import br.com.zalf.prolog.webservice.frota.checklist.model.ChecksRealizadosAbaixoTempoEspecifico;
import br.com.zalf.prolog.webservice.frota.checklist.model.QuantidadeChecklists;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 18/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class DashboardChecklistComponentsCreator {

    private DashboardChecklistComponentsCreator() {
        throw new IllegalStateException(DashboardChecklistComponentsCreator.class.getSimpleName() + " cannot be " +
                "instatiated!");
    }

    @NotNull
    static HorizontalLineChartComponent createQtdChecksUltimos30DiasByTipo(
            @NotNull final ComponentDataHolder component,
            @NotNull final List<QuantidadeChecklists> checklistsDia) {

        final Map<Double, String> informacoesPontos = new HashMap<>(checklistsDia.size());
        final Map<Double, String> representacoesValoresX = new HashMap<>(checklistsDia.size());
        final List<LineEntry> entriesSaida = new ArrayList<>();
        final List<LineEntry> entriesRetorno = new ArrayList<>();
        for (int i = 0; i < checklistsDia.size(); i++) {
            final QuantidadeChecklists qtdChecklists = checklistsDia.get(i);
            final LineEntry saida = new LineEntry(
                    qtdChecklists.getTotalChecklistsSaida(),
                    i,
                    String.valueOf(qtdChecklists.getTotalChecklistsSaida()),
                    qtdChecklists.getDataFormatada(),
                    null);
            final LineEntry retorno = new LineEntry(
                    qtdChecklists.getTotalChecklistsRetorno(),
                    i,
                    String.valueOf(qtdChecklists.getTotalChecklistsRetorno()),
                    qtdChecklists.getDataFormatada(),
                    null);
            entriesSaida.add(saida);
            entriesRetorno.add(retorno);

            // Cria a informação do ponto no gráfico em linhas.
            String informacaoPonto = qtdChecklists.getDataFormatada();
            if (qtdChecklists.teveChecklistsRealizados()) {
                if (qtdChecklists.getTotalChecklistsSaida() > 0) {
                    informacaoPonto = String.format("%s\nSaída: %d", informacaoPonto, qtdChecklists.getTotalChecklistsSaida());

                }
                if (qtdChecklists.getTotalChecklistsRetorno() > 0) {
                    informacaoPonto = String.format("%s\nRetorno: %d", informacaoPonto, qtdChecklists.getTotalChecklistsRetorno());
                }
            } else {
                informacaoPonto = String.format("%s\nsem checklists", informacaoPonto);
            }
            informacoesPontos.put((double) i, informacaoPonto);
            representacoesValoresX.put((double) i, qtdChecklists.getDataFormatada());
        }

        final List<LineGroup> groups = new ArrayList<>(2 /* saída e retorno */);
        final LineGroup groupSaida = new LineGroup("Saída", entriesSaida, Color.fromHex("#185887"));
        final LineGroup groupRetorno = new LineGroup("Retorno", entriesRetorno, Color.fromHex("#EDA444"));
        groups.add(groupSaida);
        groups.add(groupRetorno);

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


    @NotNull
    static TableComponent createChecksRealizadosAbaixo130(
            @NotNull final ComponentDataHolder component,
            @NotNull final List<ChecksRealizadosAbaixoTempoEspecifico> checksRealizadosAbaixo130) {
        // Header.
        final List<TableItemHeader> itemHeaders = new ArrayList<>(4);
        itemHeaders.add(new TableItemHeader("Unidade", null));
        itemHeaders.add(new TableItemHeader("Colaborador", null));
        itemHeaders.add(new TableItemHeader("Checks com menos de 1:30", null));
        itemHeaders.add(new TableItemHeader("Checks nos últimos 30 dias", null));
        final TableHeader tableHeader = new TableHeader(itemHeaders);

        // Linhas.
        final List<TableLine> lines = new ArrayList<>();
        checksRealizadosAbaixo130.forEach(checksRealizados -> {
            // Colunas.
            final List<TableColumn> columns = new ArrayList<>(4);
            columns.add(new TableColumn(checksRealizados.getNomeUnidade()));
            columns.add(new TableColumn(checksRealizados.getNomeColaborador()));
            columns.add(new TableColumn(String.valueOf(checksRealizados.getQtdChecksRealizadosAbaixoTempoEspecifico())));
            columns.add(new TableColumn(String.valueOf(checksRealizados.getQtdChecksRealizados())));
            lines.add(new TableLine(columns));
        });

        final TableData tableData = new TableData(lines);
        return TableComponent.createDefault(component, tableHeader, tableData, null);
    }
}