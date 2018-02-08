package br.com.zalf.prolog.webservice.dashboard.components.densitychart;

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
public class DensityChartComponent extends DashboardComponent {
    @NotNull
    private String labelEixoX;
    @NotNull
    private String labelEixoY;
    @NotNull
    private DensityData data;

    private DensityChartComponent(@NotNull Integer codigo,
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
                                  @NotNull DensityData data) {
        super(codigo, IdentificadorTipoComponente.GRAFICO_DENSIDADE, titulo, subtitulo, descricao, urlEndpointDados,
                codTipoComponente, qtdBlocosHorizontais, qtdBlocosVerticais, ordemExibicao);
        this.labelEixoX = labelEixoX;
        this.labelEixoY = labelEixoY;
        this.data = data;
    }

    @NotNull
    public String getLabelEixoX() {
        return labelEixoX;
    }

    public void setLabelEixoX(@NotNull String labelEixoX) {
        this.labelEixoX = labelEixoX;
    }

    @NotNull
    public String getLabelEixoY() {
        return labelEixoY;
    }

    public void setLabelEixoY(@NotNull String labelEixoY) {
        this.labelEixoY = labelEixoY;
    }

    @NotNull
    public DensityData getData() {
        return data;
    }

    public void setData(@NotNull DensityData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DensityChartComponent{" +
                "labelEixoX='" + labelEixoX + '\'' +
                ", labelEixoY='" + labelEixoY + '\'' +
                ", densityData=" + data +
                '}';
    }

    public static class Builder extends BaseComponentBuilder {
        private String labelEixoX;
        private String labelEixoY;
        private DensityData densityData;

        public Builder() {

        }

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

        public Builder withDensityData(@NotNull DensityData data) {
            this.densityData = data;
            return this;
        }

        @Override
        public DensityChartComponent build() {
            ensureNotNullValues();
            Preconditions.checkNotNull(labelEixoX, "labelEixoX deve ser instanciada com 'withLabelEixoX'");
            Preconditions.checkNotNull(labelEixoY, "labelEixoY deve ser instanciada com 'withLabelEixoY'");
            Preconditions.checkNotNull(densityData, "densityData deve ser instanciada com 'withDensityData'");
            return new DensityChartComponent(
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
                    densityData);
        }
    }
}