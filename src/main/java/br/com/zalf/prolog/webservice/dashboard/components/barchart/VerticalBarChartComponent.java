package br.com.zalf.prolog.webservice.dashboard.components.barchart;

import br.com.zalf.prolog.webservice.dashboard.base.BaseComponentBuilder;
import br.com.zalf.prolog.webservice.dashboard.base.DashboardComponent;
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

    public VerticalBarChartComponent(@NotNull String titulo,
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
        super(titulo, subtitulo, descricao, urlEndpointDados, codTipoComponente, qtdBlocosHorizontais,
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

        @Override
        public DashboardComponent build() {
            ensureNotNullValues();
            return null;
        }
    }
}