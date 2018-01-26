package br.com.zalf.prolog.webservice.dashboard.components.barchart;

import br.com.zalf.prolog.webservice.dashboard.base.BaseComponentBuilder;
import br.com.zalf.prolog.webservice.dashboard.base.DashboardComponent;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class VerticalBarChartComponent extends DashboardComponent {
    private static final String TIPO = "GRAFICO_BARRAS_VERTICAIS";
    @NotNull
    private String labelEixoX;
    @NotNull
    private String labelEixoY;
    @Nullable
    private Double meta;
    @NotNull
    private BarData barData;

    private VerticalBarChartComponent(@NotNull String titulo,
                                      @Nullable String subtitulo,
                                      @NotNull String descricao,
                                      @NotNull String urlEndpointDados,
                                      @NotNull Integer codTipoComponente,
                                      int qtdBlocosHorizontais,
                                      int qtdBlocosVerticais,
                                      int ordemExibicao,
                                      @NotNull String labelEixoX,
                                      @NotNull String labelEixoY,
                                      @Nullable Double meta,
                                      @NotNull BarData barData) {
        super(TIPO, titulo, subtitulo, descricao, urlEndpointDados, codTipoComponente, qtdBlocosHorizontais,
                qtdBlocosVerticais, ordemExibicao);
        this.labelEixoX = labelEixoX;
        this.labelEixoY = labelEixoY;
        this.meta = meta;
        this.barData = barData;
    }

    public static class Builder extends BaseComponentBuilder {
        private String labelEixoX;
        private String labelEixoY;
        private Double meta;
        private BarData barData;

        @Override
        public Builder withTitulo(@NotNull String titulo) {
            super.withTitulo(titulo);
            return this;
        }

        @Override
        public Builder withSubtitulo(@Nullable String subtitulo) {
            super.withSubtitulo(subtitulo);
            return this;
        }

        @Override
        public Builder withDescricao(@NotNull String descricao) {
            super.withDescricao(descricao);
            return this;
        }

        @Override
        public Builder withUrlEndpointDados(@NotNull String urlEndpointDados) {
            super.withUrlEndpointDados(urlEndpointDados);
            return this;
        }

        @Override
        public Builder withQtdBlocosHorizontais(int qtdBlocosHorizontais) {
            super.withQtdBlocosHorizontais(qtdBlocosHorizontais);
            return this;
        }

        @Override
        public Builder withCodTipoComponente(@NotNull Integer codTipoComponente) {
            super.withCodTipoComponente(codTipoComponente);
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

        public Builder withMeta(@NotNull Double meta) {
            this.meta = meta;
            return this;
        }

        public Builder withBarData(@NotNull BarData barData) {
            this.barData = barData;
            return this;
        }

        @Override
        public VerticalBarChartComponent build() {
            ensureNotNullValues();
            Preconditions.checkNotNull(labelEixoX, "labelEixoX deve ser instanciada com 'withLabelEixoX'");
            Preconditions.checkNotNull(labelEixoY, "labelEixoY deve ser instanciada com 'withLabelEixoY'");
            Preconditions.checkNotNull(barData, "barData deve ser instanciada com 'withBarData'");
            return new VerticalBarChartComponent(
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
                    meta,
                    barData);
        }
    }
}