package br.com.zalf.prolog.webservice.dashboard.components.charts.line;

import br.com.zalf.prolog.webservice.dashboard.base.IdentificadorTipoComponente;
import br.com.zalf.prolog.webservice.dashboard.components.charts.ChartComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 18/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class HorizontalLineChartComponent extends ChartComponent {
    @NotNull
    private final String labelEixoX;
    @NotNull
    private final String labelEixoY;
    @Nullable
    private final List<HorizontalLimitLine> limitLines;
    @NotNull
    private final LineData lineData;

    private HorizontalLineChartComponent(@NotNull final Integer codigo,
                                         @NotNull final String titulo,
                                         @Nullable final String subtitulo,
                                         @NotNull final String descricao,
                                         @NotNull final String urlEndpointDados,
                                         @NotNull final Integer codTipoComponente,
                                         final int qtdBlocosHorizontais,
                                         final int qtdBlocosVerticais,
                                         final int ordemExibicao,
                                         @NotNull final String labelEixoX,
                                         @NotNull final String labelEixoY,
                                         @Nullable final List<HorizontalLimitLine> limitLines,
                                         @NotNull final LineData lineData,
                                         @NotNull final LinesOrientation orientation) {
        super(codigo,
                orientation == LinesOrientation.HORIZONTAL
                        ? IdentificadorTipoComponente.GRAFICO_LINHAS_HORIZONTAIS
                        : IdentificadorTipoComponente.GRAFICO_LINHAS_VERTICAIS,
                titulo,
                subtitulo,
                descricao,
                urlEndpointDados,
                codTipoComponente,
                qtdBlocosHorizontais,
                qtdBlocosVerticais,
                ordemExibicao);
        this.labelEixoX = labelEixoX;
        this.labelEixoY = labelEixoY;
        this.limitLines = limitLines;
        this.lineData = lineData;
    }
}