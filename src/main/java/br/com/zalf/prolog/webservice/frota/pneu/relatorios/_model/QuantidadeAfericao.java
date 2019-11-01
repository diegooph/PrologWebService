package br.com.zalf.prolog.webservice.frota.pneu.relatorios._model;

import java.util.Date;

/**
 * Created by Zart on 08/01/2018.
 */
public class QuantidadeAfericao {
    private Date data;
    private String dataFormatada;
    private int qtdAfericoesSulco;
    private int qtdAfericoesPressao;
    private int qtdAfericoesSulcoPressao;

    public QuantidadeAfericao() {

    }

    public QuantidadeAfericao(Date data,
                              String dataFormatada,
                              int qtdAfericoesSulco,
                              int qtdAfericoesPressao,
                              int qtAfericaoSulcoPressao) {
        this.data = data;
        this.dataFormatada = dataFormatada;
        this.qtdAfericoesSulco = qtdAfericoesSulco;
        this.qtdAfericoesPressao = qtdAfericoesPressao;
        this.qtdAfericoesSulcoPressao = qtAfericaoSulcoPressao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getDataFormatada() {
        return dataFormatada;
    }

    public void setDataFormatada(String dataFormatada) {
        this.dataFormatada = dataFormatada;
    }

    public int getQtdAfericoesSulco() {
        return qtdAfericoesSulco;
    }

    public void setQtdAfericoesSulco(int qtdAfericoesSulco) {
        this.qtdAfericoesSulco = qtdAfericoesSulco;
    }

    public int getQtdAfericoesPressao() {
        return qtdAfericoesPressao;
    }

    public void setQtdAfericoesPressao(int qtdAfericoesPressao) {
        this.qtdAfericoesPressao = qtdAfericoesPressao;
    }

    public int getQtdAfericoesSulcoPressao() {
        return qtdAfericoesSulcoPressao;
    }

    public void setQtdAfericoesSulcoPressao(int qtdAfericoesSulcoPressao) {
        this.qtdAfericoesSulcoPressao = qtdAfericoesSulcoPressao;
    }

    public boolean teveAfericoesRealizadas() {
        return qtdAfericoesPressao > 0 || qtdAfericoesSulco > 0 || qtdAfericoesSulcoPressao > 0;
    }
}