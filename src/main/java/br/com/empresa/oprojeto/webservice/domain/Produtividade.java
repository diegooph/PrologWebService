package br.com.empresa.oprojeto.webservice.domain;


import java.util.Date;

import br.com.empresa.oprojeto.webservice.domain.indicadores.Indicador;

public class Produtividade {
    private Date data;
    // Tanto em reais ganho no dia
    private int valor;
    private Indicador devolucaoCaixa;
    private Indicador devolucaoNf;
    private Indicador jornada;
    private Indicador tempoInterno;

    public Produtividade() {
    }

    public Produtividade(Date data, int valor, Indicador devolucaoCaixa, Indicador devolucaoNf, Indicador jornada, Indicador tempoInterno) {
        this.data = data;
        this.valor = valor;
        this.devolucaoCaixa = devolucaoCaixa;
        this.devolucaoNf = devolucaoNf;
        this.jornada = jornada;
        this.tempoInterno = tempoInterno;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public Indicador getDevolucaoCaixa() {
        return devolucaoCaixa;
    }

    public void setDevolucaoCaixa(Indicador devolucaoCaixa) {
        this.devolucaoCaixa = devolucaoCaixa;
    }

    public Indicador getDevolucaoNf() {
        return devolucaoNf;
    }

    public void setDevolucaoNf(Indicador devolucaoNf) {
        this.devolucaoNf = devolucaoNf;
    }

    public Indicador getJornada() {
        return jornada;
    }

    public void setJornada(Indicador jornada) {
        this.jornada = jornada;
    }

    public Indicador getTempoInterno() {
        return tempoInterno;
    }

    public void setTempoInterno(Indicador tempoInterno) {
        this.tempoInterno = tempoInterno;
    }
}
