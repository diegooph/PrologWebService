package br.com.zalf.prolog.webservice.commons.dashboard.components.densitychart;

import br.com.zalf.prolog.webservice.commons.dashboard.base.DashboardComponent;
import br.com.zalf.prolog.webservice.commons.dashboard.base.DashboardComponentBuilder;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    private List<String> legendas;
    @NotNull
    private DensityData data;

    DensityChartComponent(@NotNull String titulo,
                          @Nullable String subtitulo,
                          @NotNull String descricao,
                          @NotNull String labelEixoX,
                          @NotNull String labelEixoY,
                          @NotNull List<String> legendas,
                          @NotNull DensityData data) {
        super(titulo, subtitulo, descricao);
        this.labelEixoX = labelEixoX;
        this.labelEixoY = labelEixoY;
        this.legendas = legendas;
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
    public List<String> getLegendas() {
        return legendas;
    }

    public void setLegendas(@NotNull List<String> legendas) {
        this.legendas = legendas;
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
                ", legendas=" + legendas +
                ", data=" + data +
                '}';
    }

    public static class Builder implements DashboardComponentBuilder {
        private String titulo;
        private String subtitulo;
        private String descricao;
        private String labelEixoX;
        private String labelEixoY;
        private DensityData data;
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

        public Builder withLabelEixoX(@NotNull String labelEixoX) {
            this.labelEixoX = labelEixoX;
            return this;
        }

        public Builder withLabelEixoY(@NotNull String labelEixoY) {
            this.labelEixoY = labelEixoY;
            return this;
        }

        public Builder withDensityData(@NotNull DensityData data) {
            this.data = data;
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
            Preconditions.checkNotNull(data, "data deve ser instanciada com 'withDensityData'");
            Preconditions.checkNotNull(legendas, "meta deve ser instanciada com 'withLegendas'");
            return new DensityChartComponent(titulo, subtitulo, descricao,
                    labelEixoX, labelEixoY, legendas, data);
        }
    }
}
