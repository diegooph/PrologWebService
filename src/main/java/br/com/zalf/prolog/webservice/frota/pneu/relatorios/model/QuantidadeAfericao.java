package br.com.zalf.prolog.webservice.frota.pneu.relatorios.model;

import java.util.Date;

/**
 * Created by Zart on 08/01/2018.
 */
public class QuantidadeAfericao {
    private Date data;
    private String dataFormatada;
    private int qtdAfericoesPressao;
    private int qtdAfericoesSulco;
    private int qtdAfericoesSulcoPressao;

    public QuantidadeAfericao() {

    }

    public QuantidadeAfericao(Date data,
                              String dataFormatada,
                              int qtdAfericoesPressao,
                              int qtdAfericoesSulco,
                              int qtAfericaoSulcoPressao) {
        this.data = data;
        this.dataFormatada = dataFormatada;
        this.qtdAfericoesPressao = qtdAfericoesPressao;
        this.qtdAfericoesSulco = qtdAfericoesSulco;
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

    public int getQtdAfericoesPressao() {
        return qtdAfericoesPressao;
    }

    public void setQtdAfericoesPressao(int qtdAfericoesPressao) {
        this.qtdAfericoesPressao = qtdAfericoesPressao;
    }

    public int getQtdAfericoesSulco() {
        return qtdAfericoesSulco;
    }

    public void setQtdAfericoesSulco(int qtdAfericoesSulco) {
        this.qtdAfericoesSulco = qtdAfericoesSulco;
    }

    public int getQtdAfericoesSulcoPressao() {
        return qtdAfericoesSulcoPressao;
    }

    public void setQtdAfericoesSulcoPressao(int qtdAfericoesSulcoPressao) {
        this.qtdAfericoesSulcoPressao = qtdAfericoesSulcoPressao;
    }
}