package br.com.zalf.prolog.webservice.dashboard.components.barchart;

import br.com.zalf.prolog.webservice.dashboard.base.BaseComponentBuilder;
import br.com.zalf.prolog.webservice.dashboard.base.DashboardComponent;
import br.com.zalf.prolog.webservice.dashboard.base.DashboardComponentBuilder;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
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
    private List<String> legendas;

    public VerticalBarChartComponent(@NotNull String titulo, @Nullable String subtitulo, @NotNull String descricao, @NotNull String urlEndpointDados, @NotNull Integer codTipoComponente, int qtdBlocosHorizontais, int qtdBlocosVerticais, int ordem, @NotNull String labelEixoX, @NotNull String labelEixoY, Double meta, @NotNull BarData barData, @NotNull List<String> legendas) {
        super(titulo, subtitulo, descricao, urlEndpointDados, codTipoComponente, qtdBlocosHorizontais, qtdBlocosVerticais, ordem);
        this.labelEixoX = labelEixoX;
        this.labelEixoY = labelEixoY;
        this.meta = meta;
        this.barData = barData;
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
    public BarData getBarData() {
        return barData;
    }

    public void setBarData(@NotNull BarData barData) {
        this.barData = barData;
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
        return "VerticalBarChartComponent{" +
                "labelEixoX='" + labelEixoX + '\'' +
                ", labelEixoY='" + labelEixoY + '\'' +
                ", meta=" + meta +
                ", barData=" + barData +
                ", legendas=" + legendas +
                '}';
    }

    public static class Builder extends BaseComponentBuilder {
        private String titulo;
        private String subtitulo;
        private String descricao;
        private String labelEixoX;
        private String labelEixoY;
        private Double meta;
        private BarData barData;
        private List<String> legendas;

        public Builder() {}

        public Builder(@NotNull String titulo, @NotNull String descricao) {
            this.titulo = titulo;
            this.descricao = descricao;
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
        public DashboardComponentBuilder withUrlEndpointDados(@NotNull String urlEndpointDados) {
            return null;
        }

        @Override
        public DashboardComponentBuilder withCodTipoComponente(@NotNull Integer codTipoComponente) {
            return null;
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

        public Builder withLegendas(@NotNull List<String> legendas) {
            this.legendas = legendas;
            return this;
        }

        @Override
        public DashboardComponent build() {
            Preconditions.checkNotNull(labelEixoX, "labelEixoX deve ser instanciada com 'withLabelEixoX'");
            Preconditions.checkNotNull(labelEixoY, "labelEixoY deve ser instanciada com 'withLabelEixoY'");
            Preconditions.checkNotNull(meta, "meta deve ser instanciada com 'withMeta'");
            Preconditions.checkNotNull(barData, "meta deve ser instanciada com 'withBarData'");
            Preconditions.checkNotNull(legendas, "meta deve ser instanciada com 'withLegendas'");
            return null;
        }
    }
}
