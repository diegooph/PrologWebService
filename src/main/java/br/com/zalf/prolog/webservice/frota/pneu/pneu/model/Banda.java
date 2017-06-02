package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;

/**
 * Created by Zart on 04/04/17.
 */
public class Banda {

    private Marca marca;
    private ModeloBanda modelo;

    public Banda() {
    }

    public Banda(Marca marca, ModeloBanda modelo) {
        this.marca = marca;
        this.modelo = modelo;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public ModeloBanda getModelo() {
        return modelo;
    }

    public void setModelo(ModeloBanda modelo) {
        this.modelo = modelo;
    }

}
