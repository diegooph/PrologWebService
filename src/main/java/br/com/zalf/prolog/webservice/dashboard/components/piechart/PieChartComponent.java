package br.com.zalf.prolog.webservice.dashboard.components.piechart;


import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.base.BaseComponentBuilder;
import br.com.zalf.prolog.webservice.dashboard.base.DashboardComponent;
import br.com.zalf.prolog.webservice.dashboard.base.IdentificadorTipoComponente;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PieChartComponent extends DashboardComponent {
    @NotNull
    private PieData pieData;

    public static PieChartComponent createDefault(@NotNull final ComponentDataHolder component,
                                                  @NotNull final PieData pieData) {
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

    private PieChartComponent(@NotNull String titulo,
                              @Nullable String subtitulo,
                              @NotNull String descricao,
                              @NotNull String urlEndpointDados,
                              @NotNull Integer codTipoComponente,
                              int qtdBlocosHorizontais,
                              int qtdBlocosVerticais,
                              int ordem,
                              @NotNull PieData pieData) {
        super(IdentificadorTipoComponente.GRAFICO_SETORES, titulo, subtitulo, descricao, urlEndpointDados, codTipoComponente, qtdBlocosHorizontais, qtdBlocosVerticais, ordem);
        this.pieData = pieData;
    }

    @NotNull
    public PieData getPieData() {
        return pieData;
    }

    public void setPieData(@NotNull PieData pieData) {
        this.pieData = pieData;
    }

    @Override
    public String toString() {
        return "PieChartComponent{" +
                "pieData=" + pieData +
                '}';
    }

    public static class Builder extends BaseComponentBuilder {
        private PieData pieData;

        public Builder() {}

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

        @Override
        public PieChartComponent build() {
            return new PieChartComponent(
                    titulo,
                    subtitulo,
                    descricao,
                    urlEndpointDados,
                    codTipoComponente,
                    qtdBlocosHorizontais,
                    qtdBlocosVerticais,
                    ordemExibicao,
                    pieData);
        }

        @Override
        protected void ensureNotNullValues() {
            super.ensureNotNullValues();
            Preconditions.checkNotNull(pieData, "pieData deve ser instanciada com 'withPieData'");
        }
    }
}
