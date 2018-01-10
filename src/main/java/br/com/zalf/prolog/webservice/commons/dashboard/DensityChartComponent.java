package br.com.zalf.prolog.webservice.commons.dashboard;

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

    public DensityChartComponent(@NotNull String titulo,
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
}
