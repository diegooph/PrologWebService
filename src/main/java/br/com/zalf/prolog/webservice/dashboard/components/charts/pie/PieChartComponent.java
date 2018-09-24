package br.com.zalf.prolog.webservice.dashboard.components.charts.pie;


import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.base.BaseComponentBuilder;
import br.com.zalf.prolog.webservice.dashboard.base.IdentificadorTipoComponente;
import br.com.zalf.prolog.webservice.dashboard.components.charts.ChartComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PieChartComponent extends ChartComponent {
    @NotNull
    private final PieData pieData;
    @NotNull
    private final SliceValueMode sliceValueMode;

    public static PieChartComponent createDefault(@NotNull final ComponentDataHolder component,
                                                  @NotNull final PieData pieData,
                                                  @NotNull final SliceValueMode sliceValueMode) {
        return new PieChartComponent.Builder()
                .withCodigo(component.codigoComponente)
                .withTitulo(component.tituloComponente)
                .withSubtitulo(component.subtituloComponente)
                .withDescricao(component.descricaoComponente)
                .withCodTipoComponente(component.codigoTipoComponente)
                .withUrlEndpointDados(component.urlEndpointDados)
                .withQtdBlocosHorizontais(component.qtdBlocosHorizontais)
                .withQtdBlocosVerticais(component.qtdBlocosVerticais)
                .withOrdemExibicao(component.ordemExibicao)
                .withPieData(pieData)
                .withSliceValueMode(sliceValueMode)
                .build();
    }

    private PieChartComponent(@NotNull final Integer codigo,
                              @NotNull final String titulo,
                              @Nullable final String subtitulo,
                              @NotNull final String descricao,
                              @NotNull final String urlEndpointDados,
                              @NotNull final Integer codTipoComponente,
                              final int qtdBlocosHorizontais,
                              final int qtdBlocosVerticais,
                              final int ordem,
                              @NotNull final PieData pieData,
                              @NotNull final SliceValueMode sliceValueMode) {
        super(codigo, IdentificadorTipoComponente.GRAFICO_SETORES, titulo, subtitulo, descricao, urlEndpointDados,
                codTipoComponente, qtdBlocosHorizontais, qtdBlocosVerticais, ordem);
        this.pieData = pieData;
        this.sliceValueMode = sliceValueMode;
    }

    public static class Builder extends BaseComponentBuilder {
        private PieData pieData;
        private SliceValueMode sliceValueMode;

        public Builder() {}

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

        public Builder withPieData(@NotNull PieData pieData) {
            this.pieData = pieData;
            return this;
        }

        public Builder withSliceValueMode(@NotNull SliceValueMode sliceValueMode) {
            this.sliceValueMode = sliceValueMode;
            return this;
        }

        @Override
        public PieChartComponent build() {
            ensureNotNullValues();
            checkNotNull(pieData, "pieData deve ser instanciada com 'withPieData'");
            checkNotNull(sliceValueMode, "sliceValueMode deve ser instanciada com 'withSliceValueMode'");
            return new PieChartComponent(
                    codigo,
                    titulo,
                    subtitulo,
                    descricao,
                    urlEndpointDados,
                    codTipoComponente,
                    qtdBlocosHorizontais,
                    qtdBlocosVerticais,
                    ordemExibicao,
                    pieData,
                    sliceValueMode);
        }
    }
}