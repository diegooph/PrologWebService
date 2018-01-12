package br.com.zalf.prolog.webservice.commons.dashboard;


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

    public PieChartComponent(@NotNull String titulo,
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
}
