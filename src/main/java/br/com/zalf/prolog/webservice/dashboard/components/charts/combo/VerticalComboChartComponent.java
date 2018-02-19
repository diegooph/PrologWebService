package br.com.zalf.prolog.webservice.dashboard.components.charts.combo;

import br.com.zalf.prolog.webservice.dashboard.base.BaseComponentBuilder;
import br.com.zalf.prolog.webservice.dashboard.base.IdentificadorTipoComponente;
import br.com.zalf.prolog.webservice.dashboard.components.charts.ChartComponent;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class VerticalComboChartComponent extends ChartComponent {
    @NotNull
    private String labelEixoX;
    @NotNull
    private String labelEixoY;
    @Nullable
    private Double meta;
    @NotNull
    private ComboData comboData;
    @NotNull
    private List<String> legendas;

    private VerticalComboChartComponent(@NotNull Integer codigo,
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
                                        @NotNull ComboData comboData,
                                        @NotNull List<String> legendas) {
        super(codigo, IdentificadorTipoComponente.GRAFICO_BARRAS_VERTICAIS_AGRUPADAS, titulo, subtitulo, descricao,
                urlEndpointDados, codTipoComponente, qtdBlocosHorizontais, qtdBlocosVerticais, ordemExibicao);
        this.labelEixoX = labelEixoX;
        this.labelEixoY = labelEixoY;
        this.meta = meta;
        this.comboData = comboData;
        this.legendas = legendas;
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

    @Nullable
    public Double getMeta() {
        return meta;
    }

    public void setMeta(@Nullable Double meta) {
        this.meta = meta;
    }

    @NotNull
    public ComboData getComboData() {
        return comboData;
    }

    public void setComboData(@NotNull ComboData comboData) {
        this.comboData = comboData;
    }

    @NotNull
    public List<String> getLegendas() {
        return legendas;
    }

    public void setLegendas(@NotNull List<String> legendas) {
        this.legendas = legendas;
    }

    @Override
    public String toString() {
        return "VerticalComboChartComponent{" +
                "labelEixoX='" + labelEixoX + '\'' +
                ", labelEixoY='" + labelEixoY + '\'' +
                ", meta=" + meta +
                ", comboData=" + comboData +
                ", legendas=" + legendas +
                '}';
    }

    public static class Builder extends BaseComponentBuilder {
        private String labelEixoX;
        private String labelEixoY;
        private Double meta;
        private ComboData comboData;
        private List<String> legendas;

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

        public Builder withMeta(@NotNull Double meta) {
            this.meta = meta;
            return this;
        }

        public Builder withComboData(@NotNull ComboData comboData) {
            this.comboData = comboData;
            return this;
        }

        public Builder withLegendas(@NotNull List<String> legendas) {
            this.legendas = legendas;
            return this;
        }

        @Override
        public VerticalComboChartComponent build() {
            ensureNotNullValues();
            Preconditions.checkNotNull(labelEixoX, "labelEixoX deve ser instanciada com 'withLabelEixoX'");
            Preconditions.checkNotNull(labelEixoY, "labelEixoY deve ser instanciada com 'withLabelEixoY'");
            Preconditions.checkNotNull(comboData, "comboData deve ser instanciada com 'withComboData'");
            Preconditions.checkNotNull(legendas, "legendas deve ser instanciada com 'withLegendas'");
            return new VerticalComboChartComponent(
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
                    comboData,
                    legendas);
        }
    }
}
