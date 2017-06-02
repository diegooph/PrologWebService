package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;


import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;

/**
 * Created by Zart on 02/06/2017.
 */
public class ModeloPneu extends Modelo {

    private int quantidadeSulcos;
    private double valor;

    public ModeloPneu() {
    }

    public int getQuantidadeSulcos() {
        return quantidadeSulcos;
    }

    public void setQuantidadeSulcos(int quantidadeSulcos) {
        this.quantidadeSulcos = quantidadeSulcos;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
