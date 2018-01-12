package br.com.zalf.prolog.webservice.frota.pneu.relatorios.model;

import java.util.Date;

/**
 * Created by Zart on 08/01/2018.
 */
public class QtAfericao {

    private Date data;
    private int qtAfericaoPressao;
    private int qtAfericaoSulco;
    private int qtAfericaoSulcoPressao;

    public QtAfericao() {
    }

    public QtAfericao(Date data, int qtAfericaoPressao, int qtAfericaoSulco, int qtAfericaoSulcoPressao) {
        this.data = data;
        this.qtAfericaoPressao = qtAfericaoPressao;
        this.qtAfericaoSulco = qtAfericaoSulco;
        this.qtAfericaoSulcoPressao = qtAfericaoSulcoPressao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public int getQtAfericaoPressao() {
        return qtAfericaoPressao;
    }

    public void setQtAfericaoPressao(int qtAfericaoPressao) {
        this.qtAfericaoPressao = qtAfericaoPressao;
    }

    public int getQtAfericaoSulco() {
        return qtAfericaoSulco;
    }

    public void setQtAfericaoSulco(int qtAfericaoSulco) {
        this.qtAfericaoSulco = qtAfericaoSulco;
    }

    public int getQtAfericaoSulcoPressao() {
        return qtAfericaoSulcoPressao;
    }

    public void setQtAfericaoSulcoPressao(int qtAfericaoSulcoPressao) {
        this.qtAfericaoSulcoPressao = qtAfericaoSulcoPressao;
    }
}
