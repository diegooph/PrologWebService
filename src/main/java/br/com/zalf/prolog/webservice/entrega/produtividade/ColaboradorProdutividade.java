package br.com.zalf.prolog.webservice.entrega.produtividade;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;

/**
 * Created by jean on 22/08/16.
 */
public class ColaboradorProdutividade {

    private Colaborador colaborador;
    private double valor;
    private double qtdMapas;
    private int qtdCaixas;

    public ColaboradorProdutividade() {
    }

    public int getQtdCaixas() {
        return qtdCaixas;
    }

    public void setQtdCaixas(int qtdCaixas) {
        this.qtdCaixas = qtdCaixas;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getQtdMapas() {
        return qtdMapas;
    }

    public void setQtdMapas(double qtdMapas) {
        this.qtdMapas = qtdMapas;
    }

    @Override
    public String toString() {
        return "ColaboradorProdutividade{" +
                "colaborador=" + colaborador +
                ", valor=" + valor +
                ", qtdMapas=" + qtdMapas +
                ", qtdCaixas=" + qtdCaixas +
                '}';
    }
}
