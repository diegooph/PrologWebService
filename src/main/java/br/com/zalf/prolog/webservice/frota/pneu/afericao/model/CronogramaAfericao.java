package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import java.util.List;

/**
 * Created by jean on 04/04/16.
 */
public class CronogramaAfericao {
    private List<ModeloPlacasAfericao> modelosPlacasAfericao;
    private int metaAfericaoSulco;
    private int metaAfericaoPressao;
    private int totalSulcosOk;
    private int totalPressaoOk;
    private int totalSulcoPressaoOk;
    private int totalVeiculos;

    public CronogramaAfericao() {}

    public List<ModeloPlacasAfericao> getModelosPlacasAfericao() {
        return modelosPlacasAfericao;
    }

    public void setModelosPlacasAfericao(List<ModeloPlacasAfericao> modelosPlacasAfericao) {
        this.modelosPlacasAfericao = modelosPlacasAfericao;
    }

    public int getMetaAfericaoSulco() {
        return metaAfericaoSulco;
    }

    public void setMetaAfericaoSulco(int metaAfericaoSulco) {
        this.metaAfericaoSulco = metaAfericaoSulco;
    }

    public int getMetaAfericaoPressao() {
        return metaAfericaoPressao;
    }

    public void setMetaAfericaoPressao(int metaAfericaoPressao) {
        this.metaAfericaoPressao = metaAfericaoPressao;
    }

    public int getTotalSulcosOk() {
        return totalSulcosOk;
    }

    public void setTotalSulcosOk(int totalSulcosOk) {
        this.totalSulcosOk = totalSulcosOk;
    }

    public int getTotalPressaoOk() {
        return totalPressaoOk;
    }

    public void setTotalPressaoOk(int totalPressaoOk) {
        this.totalPressaoOk = totalPressaoOk;
    }

    public int getTotalSulcoPressaoOk() {
        return totalSulcoPressaoOk;
    }

    public void setTotalSulcoPressaoOk(int totalSulcoPressaoOk) {
        this.totalSulcoPressaoOk = totalSulcoPressaoOk;
    }

    public int getTotalVeiculos() {
        return totalVeiculos;
    }

    public void setTotalVeiculos(int totalVeiculos) {
        this.totalVeiculos = totalVeiculos;
    }

    @Override
    public String toString() {
        return "CronogramaAfericao{" +
                "modelosPlacasAfericao=" + modelosPlacasAfericao +
                ", metaAfericaoSulco=" + metaAfericaoSulco +
                ", metaAfericaoPressao=" + metaAfericaoPressao +
                ", totalSulcosOk=" + totalSulcosOk +
                ", totalPressaoOk=" + totalPressaoOk +
                ", totalSulcoPressaoOk=" + totalSulcoPressaoOk +
                ", totalVeiculos=" + totalVeiculos +
                '}';
    }
}
