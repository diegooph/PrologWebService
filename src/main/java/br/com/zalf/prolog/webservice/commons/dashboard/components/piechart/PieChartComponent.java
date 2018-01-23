package br.com.zalf.prolog.webservice.commons.dashboard.components.piechart;


import br.com.zalf.prolog.webservice.commons.dashboard.base.DashboardComponent;
import br.com.zalf.prolog.webservice.commons.dashboard.base.DashboardComponentBuilder;
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

    public static class Builder implements DashboardComponentBuilder {
        private String titulo;
        private String subtitulo;
        private String descricao;
        private PieData pieData;

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

        public Builder withPieData(@NotNull PieData pieData) {
            this.pieData = pieData;
            return this;
        }

        @Override
        public DashboardComponent build() {
            Preconditions.checkNotNull(pieData, "pieData deve ser instanciada com 'withPieData'");
            return new PieChartComponent(titulo, subtitulo, descricao, pieData);
        }
    }
}
