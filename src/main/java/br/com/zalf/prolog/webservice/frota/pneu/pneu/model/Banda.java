package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;

import java.math.BigDecimal;

/**
 * @deprecated em 2019-10-11 por conta da criação de objetos específicos para o crud de bandas e modelos de pneu.
 * Esse objeto precisa ser refatorado.
 */
@Deprecated
public class Banda {

    private Marca marca;
    private ModeloBanda modelo;
    private BigDecimal valor;

    public Banda() {
    }

    public Banda(Marca marca, ModeloBanda modelo) {
        this.marca = marca;
        this.modelo = modelo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor.setScale(2, BigDecimal.ROUND_HALF_UP);
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

    @Override
    public String toString() {
        return "Banda{" +
                "marca=" + marca +
                ", modelo=" + modelo +
                ", valor=" + valor +
                '}';
    }
}
