package br.com.zalf.prolog.webservice.frota.pneu.relatorios.model;

/**
 * Created on 1/26/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class SulcoPressao {
    private double valorSulco;
    private double valorPressao;

    public SulcoPressao(double valorSulco, double valorPressao) {
        this.valorSulco = valorSulco;
        this.valorPressao = valorPressao;
    }

    public double getValorSulco() {
        return valorSulco;
    }

    public void setValorSulco(double valorSulco) {
        this.valorSulco = valorSulco;
    }

    public double getValorPressao() {
        return valorPressao;
    }

    public void setValorPressao(double valorPressao) {
        this.valorPressao = valorPressao;
    }
}