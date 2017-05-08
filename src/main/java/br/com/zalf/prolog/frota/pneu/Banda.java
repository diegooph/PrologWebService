package br.com.zalf.prolog.frota.pneu;

import br.com.zalf.prolog.commons.veiculo.Marca;
import br.com.zalf.prolog.commons.veiculo.Modelo;

/**
 * Created by Zart on 04/04/17.
 */
public class Banda {

    private Marca marca;
    private Modelo modelo;
    private double valor;

    public Banda() {
    }

    public Banda(Marca marca, Modelo modelo, double valor) {
        this.marca = marca;
        this.modelo = modelo;
        this.valor = valor;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

}
