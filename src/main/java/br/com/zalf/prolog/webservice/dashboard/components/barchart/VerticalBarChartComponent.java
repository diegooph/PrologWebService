package br.com.zalf.prolog.webservice.dashboard.components.barchart;

import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.base.BaseComponentBuilder;
import br.com.zalf.prolog.webservice.dashboard.base.DashboardComponent;
import br.com.zalf.prolog.webservice.dashboard.base.IdentificadorTipoComponente;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class VerticalBarChartComponent extends DashboardComponent {
    @NotNull
    private String labelEixoX;
    @NotNull
    private String labelEixoY;
    @Nullable
    private Double meta;
    @NotNull
    private BarData barData;

    @NotNull
    public static VerticalBarChartComponent createDefault(@NotNull final ComponentDataHolder component,
                                                          @NotNull final BarData barData,
                                                          @Nullable final Double meta) {
        return new VerticalBarChartComponent.Builder()
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
                .withBarData(barData)
                .withMeta(meta)
                .build();
    }

    private VerticalBarChartComponent(@NotNull Integer codigo,
                                      @NotNull String titulo,
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
        super(codigo, IdentificadorTipoComponente.GRAFICO_BARRAS_VERTICAIS, titulo, subtitulo, descricao,
                urlEndpointDados, codTipoComponente, qtdBlocosHorizontais, qtdBlocosVerticais, ordemExibicao);
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
        public Builder withCodigo(@NotNull Integer codigo) {
            super.withCodigo(codigo);
            return this;
        }

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

        public Builder withMeta(@Nullable Double meta) {
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
                    meta,
                    barData);
        }
    }
}