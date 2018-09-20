package br.com.zalf.prolog.webservice.dashboard.components.charts.line;

import br.com.zalf.prolog.webservice.dashboard.base.BaseComponentBuilder;
import br.com.zalf.prolog.webservice.dashboard.base.IdentificadorTipoComponente;
import br.com.zalf.prolog.webservice.dashboard.components.charts.ChartComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

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
    @Nullable
    private final Map<Double, String> informacoesPontos;

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
                                         @NotNull final LineData lineData,
                                         @NotNull final LinesOrientation orientation,
                                         @Nullable final List<HorizontalLimitLine> limitLines,
                                         @Nullable final Map<Double, String> informacoesPontos) {
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
        this.informacoesPontos = informacoesPontos;
    }

    public static class Builder extends BaseComponentBuilder {
        private String labelEixoX;
        private String labelEixoY;
        private List<HorizontalLimitLine> limitLines;
        private LineData lineData;
        private LinesOrientation linesOrientation;
        private Map<Double, String> informacoesPontos;

        public Builder() {}

        @Override
        public Builder withCodigo(@NotNull Integer codigo) {
            super.withCodigo(codigo);
            return this;
        }

        @Override
        public Builder withTitulo(@NotNull String titulo) {
            this.titulo = titulo;
            return this;
        }

        @Override
        public Builder withSubtitulo(@Nullable String subtitulo) {
            this.subtitulo = subtitulo;
            return this;
        }

        @Override
        public Builder withDescricao(@NotNull String descricao) {
            this.descricao = descricao;
            return this;
        }

        @Override
        public Builder withUrlEndpointDados(@NotNull String urlEndpointDados) {
            super.withUrlEndpointDados(urlEndpointDados);
            return this;
        }

        @Override
        public Builder withCodTipoComponente(@NotNull Integer codTipoComponente) {
            super.withCodTipoComponente(codTipoComponente);
            return this;
        }

        @Override
        public Builder withQtdBlocosHorizontais(int qtdBlocosHorizontais) {
            super.withQtdBlocosHorizontais(qtdBlocosHorizontais);
            return this;
        }

        @Override
        public Builder withQtdBlocosVerticais(int qtdBlocosVerticais) {
            super.withQtdBlocosVerticais(qtdBlocosVerticais);
            return this;
        }

        @Override
        public Builder withOrdemExibicao(int ordemExibicao) {
            super.withOrdemExibicao(ordemExibicao);
            return this;
        }

        public Builder withLabelEixoX(@NotNull String labelEixoX) {
            this.labelEixoX = labelEixoX;
            return this;
        }

        public Builder withLabelEixoY(@NotNull String labelEixoY) {
            this.labelEixoY = labelEixoY;
            return this;
        }

        public Builder withLimitLines(@NotNull List<HorizontalLimitLine> limitLines) {
            this.limitLines = limitLines;
            return this;
        }

        public Builder withLineData(@NotNull LineData lineData) {
            this.lineData = lineData;
            return this;
        }

        public Builder withInformacoesPontos(@NotNull Map<Double, String> informacoesPontos) {
            this.informacoesPontos = informacoesPontos;
            return this;
        }

        public Builder withLinesOrientation(@NotNull LinesOrientation linesOrientation) {
            this.linesOrientation = linesOrientation;
            return this;
        }

        @Override
        public HorizontalLineChartComponent build() {
            ensureNotNullValues();
            checkNotNull(labelEixoX, "labelEixoX deve ser instanciada com 'withLabelEixoX'");
            checkNotNull(labelEixoY, "labelEixoY deve ser instanciada com 'withLabelEixoY'");
            checkNotNull(lineData, "lineData deve ser instanciada com 'withLineData'");
            checkNotNull(linesOrientation, "linesOrientation deve ser instanciada com 'withLinesOrientation'");

            return new HorizontalLineChartComponent(
                    codigo,
                    titulo,
                    subtitulo,
                    descricao,
                    urlEndpointDados,
                    codTipoComponente,
                    qtdBlocosHorizontais,
                    qtdBlocosVerticais,
                    ordemExibicao,
                    labelEixoX,
                    labelEixoY,
                    lineData,
                    linesOrientation,
                    limitLines,
                    informacoesPontos);
        }
    }
}