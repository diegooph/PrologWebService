package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import java.util.List;

/**
 * Created by jean on 04/04/16.
 */
public class CronogramaAfericao {

    List<PlacaModeloHolder> placas;
    private int metaAfericaoSulco;
    private int metaAfericaoPressao;
    private int qtdPlacasSulcoOk;
    private int qtdPlacasPressaoOk;
    private int qtdPlacasTudoOk;

    public CronogramaAfericao() {}

    public List<PlacaModeloHolder> getPlacas() {
        return placas;
    }

    public void setPlacas(List<PlacaModeloHolder> placas) {
        this.placas = placas;
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

    public int getQtdPlacasSulcoOk() {
        return qtdPlacasSulcoOk;
    }

    public void setQtdPlacasSulcoOk(int qtdPlacasSulcoOk) {
        this.qtdPlacasSulcoOk = qtdPlacasSulcoOk;
    }

    public int getQtdPlacasPressaoOk() {
        return qtdPlacasPressaoOk;
    }

    public void setQtdPlacasPressaoOk(int qtdPlacasPressaoOk) {
        this.qtdPlacasPressaoOk = qtdPlacasPressaoOk;
    }

    public int getQtdPlacasTudoOk() {
        return qtdPlacasTudoOk;
    }

    public void setQtdPlacasTudoOk(int qtdPlacasTudoOk) {
        this.qtdPlacasTudoOk = qtdPlacasTudoOk;
    }

    @Override
    public String toString() {
        return "CronogramaAfericao{" +
                "placas=" + placas +
                ", metaAfericaoSulco=" + metaAfericaoSulco +
                ", metaAfericaoPressao=" + metaAfericaoPressao +
                ", qtdPlacasSulcoOk=" + qtdPlacasSulcoOk +
                ", qtdPlacasPressaoOk=" + qtdPlacasPressaoOk +
                ", qtdPlacasTudoOk=" + qtdPlacasTudoOk +
                '}';
    }
}
