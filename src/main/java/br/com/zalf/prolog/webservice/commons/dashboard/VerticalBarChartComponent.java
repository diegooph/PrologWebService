package br.com.zalf.prolog.webservice.commons.dashboard;

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

    public VerticalBarChartComponent(@NotNull String titulo,
                                     @Nullable String subtitulo,
                                     @NotNull String descricao,
                                     @NotNull String labelEixoX,
                                     @NotNull String labelEixoY,
                                     @Nullable Double meta,
                                     @NotNull BarData barData,
                                     @NotNull List<String> legendas) {
        super(titulo, subtitulo, descricao);
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
}
