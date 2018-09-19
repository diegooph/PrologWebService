package br.com.zalf.prolog.webservice.frota.checklist.dashboard;

import br.com.zalf.prolog.webservice.dashboard.Color;
import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.components.charts.line.*;
import br.com.zalf.prolog.webservice.frota.checklist.model.QuantidadeChecklists;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

        final List<LineEntry> entriesSaida = new ArrayList<>();
        final List<LineEntry> entriesRetorno = new ArrayList<>();
        checklistsDia.forEach(qtdChecklists -> {
            final LineEntry saida = new LineEntry(
                    qtdChecklists.getTotalChecklistsSaida(),
                    String.valueOf(qtdChecklists.getTotalChecklistsSaida()),
                    null);
            final LineEntry retorno = new LineEntry(
                    qtdChecklists.getTotalChecklistsRetorno(),
                    String.valueOf(qtdChecklists.getTotalChecklistsRetorno()),
                    null);
            entriesSaida.add(saida);
            entriesRetorno.add(retorno);
        });

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
                .withLinesOrientation(LinesOrientation.HORIZONTAL)
                .build();
    }
}