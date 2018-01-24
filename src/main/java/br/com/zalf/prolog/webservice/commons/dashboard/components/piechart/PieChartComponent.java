package br.com.zalf.prolog.webservice.commons.dashboard.components.piechart;


import br.com.zalf.prolog.webservice.commons.dashboard.base.BaseComponentBuilder;
import br.com.zalf.prolog.webservice.commons.dashboard.base.DashboardComponent;
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

    PieChartComponent(@NotNull String titulo,
                      @Nullable String subtitulo,
                      @NotNull String descricao,
                      @NotNull PieData pieData) {
        super(titulo, subtitulo, descricao);
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

        public Builder withPieData(@NotNull PieData pieData) {
            this.pieData = pieData;
            return this;
        }

        @Override
        public PieChartComponent build() {
            return new PieChartComponent(titulo, subtitulo, descricao, pieData);
        }

        @Override
        protected void ensureNotNullValues() {
            super.ensureNotNullValues();
            Preconditions.checkNotNull(pieData, "pieData deve ser instanciada com 'withPieData'");
        }
    }
}
