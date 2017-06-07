package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;


import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;

/**
 * Created by Zart on 02/06/2017.
 */
public class ModeloBanda extends Modelo {

    private int quantidadeSulcos;

    public ModeloBanda() {
    }

    public int getQuantidadeSulcos() {
        return quantidadeSulcos;
    }

    public void setQuantidadeSulcos(int quantidadeSulcos) {
        this.quantidadeSulcos = quantidadeSulcos;
    }

}
